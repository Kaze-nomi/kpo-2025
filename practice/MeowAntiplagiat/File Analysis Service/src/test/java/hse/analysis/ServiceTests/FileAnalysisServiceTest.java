package hse.analysis.ServiceTests;

import hse.analysis.domains.Analysis;
import hse.analysis.repositories.FileRepository;
import hse.analysis.services.FileAnalysisService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileAnalysisServiceTest {

    private static MockWebServer mockWebServer;
    private FileRepository fileRepository;
    private FileAnalysisService fileAnalysisService;
    private Path testStoragePath;

    @BeforeAll
    static void setUpAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        mockWebServer.shutdown();
    }

    @BeforeEach
    void setUp() throws IOException {
        fileRepository = mock(FileRepository.class);
        testStoragePath = Files.createTempDirectory("test-storage");
        
    fileAnalysisService = new FileAnalysisService(
            fileRepository,
            WebClient.builder(),
            Optional.of(mockWebServer.url("/").toString())
    );
        
        fileAnalysisService.setStoragePath(testStoragePath);
    }

    @Test
    void analyzeFile_Success() throws Exception {
        String fileId = "123";
        String fileName = "test.txt";
        String fileContent = "Hello world\nAnother line";
        byte[] mockImage = {0x00, 0x01, 0x02};

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody(new String(mockImage)));

        when(fileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = fileAnalysisService.analyzeFile(fileId, fileName, fileContent);

        assertNotNull(result);
        assertTrue(result.contains("24"));  // Количество символов
        assertTrue(result.contains("4"));   // Количество слов
        assertTrue(result.contains("1"));   // Количество параграфов

        ArgumentCaptor<Analysis> analysisCaptor = ArgumentCaptor.forClass(Analysis.class);
        verify(fileRepository).save(analysisCaptor.capture());
        
        Analysis savedAnalysis = analysisCaptor.getValue();
        assertEquals(fileId, String.valueOf(savedAnalysis.getFileId()));
        assertNotNull(savedAnalysis.getCloudLocation());
        
        assertTrue(Files.exists(Path.of(savedAnalysis.getCloudLocation())));
    }

    @Test
    void analyzeFile_WordCloudGenerationFailure() throws Exception {
        String fileId = "123";
        String fileName = "test.txt";
        String fileContent = "content";

        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        when(fileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = fileAnalysisService.analyzeFile(fileId, fileName, fileContent);
                
        assert(result.contains("Ошибка генерации облака"));
    }

    @Test
    void getFile_ExistingAnalysis() {
        String fileId = "123";
        Analysis expectedAnalysis = new Analysis();
        expectedAnalysis.setFileId(Integer.parseInt(fileId));
        
        when(fileRepository.findById(Integer.parseInt(fileId)))
                .thenReturn(Optional.of(expectedAnalysis));

        Analysis result = fileAnalysisService.getFile(fileId);

        assertSame(expectedAnalysis, result);
    }

    @Test
    void getFile_NonExistingAnalysis() {
        String fileId = "456";
        when(fileRepository.findById(Integer.parseInt(fileId)))
                .thenReturn(Optional.empty());

        Analysis result = fileAnalysisService.getFile(fileId);

        assertNull(result);
    }

    @Test
    void getWordCloud_Success() throws Exception {
        String fileId = "123";
        String cloudPath = testStoragePath.resolve("wordcloud_test.png").toString();
        byte[] expectedBytes = {0x01, 0x02, 0x03};
        
        Analysis analysis = new Analysis();
        analysis.setCloudLocation(cloudPath);
        Files.write(Path.of(cloudPath), expectedBytes);
        
        when(fileRepository.findById(Integer.parseInt(fileId)))
                .thenReturn(Optional.of(analysis));

        byte[] result = fileAnalysisService.getWordCloud(fileId);

        assertArrayEquals(expectedBytes, result);
    }

    @Test
    void getWordCloud_AnalysisNotFound() {
        String fileId = "123";
        when(fileRepository.findById(Integer.parseInt(fileId)))
                .thenReturn(Optional.empty());

        assertThrows(IOException.class, () -> 
            fileAnalysisService.getWordCloud(fileId));
    }

    @Test
    void getWordCloud_FileNotFound() {
        String fileId = "123";
        Analysis analysis = new Analysis();
        analysis.setCloudLocation("non_existing_path");
        
        when(fileRepository.findById(Integer.parseInt(fileId)))
                .thenReturn(Optional.of(analysis));

        assertThrows(IOException.class, () -> 
            fileAnalysisService.getWordCloud(fileId));
    }

}