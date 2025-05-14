package hse.storing.ServiceTests;

import hse.storing.domains.File;
import hse.storing.repositories.FileRepository;
import hse.storing.services.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fileStorageService, "storagePath", tempDir);
    }

    @Test
    void saveFile_EmptyContent_ThrowsException() {
        byte[] content = new byte[0];
        assertThrows(IOException.class, () -> fileStorageService.saveFile("test.txt", content));
    }

    @Test
    void saveFile_DuplicateHash_ReturnsDuplicateMessage() throws IOException {
        byte[] content = "content".getBytes();
        File mockFile = new File();
        mockFile.setId(1);
        when(fileRepository.findByHash(anyInt())).thenReturn(Optional.of(mockFile));

        String result = fileStorageService.saveFile("test.txt", content);
        assertTrue(result.contains("Попался, обманщик!"));
    }

    @Test
    void saveFile_DuplicateName_AppendsSuffix() throws IOException {
        byte[] content = "content".getBytes();
        Files.write(tempDir.resolve("test.txt"), "old".getBytes());
    
        File savedFile = new File();
        savedFile.setId(1);
        when(fileRepository.save(any())).thenReturn((savedFile));

        fileStorageService.saveFile("test.txt", content);

        Optional<String> result = Files.list(tempDir)
                .filter(path -> path.getFileName().toString().contains("_SameNameAnotherContent_"))
                .map(path -> path.getFileName().toString())
                .findFirst();

        assertTrue(result.isPresent());
    }

    @Test
    void saveFile_Success_ReturnsIdAndSavesFile() throws IOException {
        byte[] content = "content".getBytes();
        File savedFile = new File();
        savedFile.setId(1);
        when(fileRepository.save(any())).thenReturn(savedFile);

        String result = fileStorageService.saveFile("test.txt", content);
        assertTrue(result.contains("ID файла: 1"));
        assertTrue(Files.exists(tempDir.resolve("test.txt")));
    }

    @Test
    void getFileName_InvalidId_ThrowsException() {
        when(fileRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(IOException.class, () -> fileStorageService.getFileName(1));
    }

    @Test
    void getFileName_ValidId_ReturnsName() throws IOException {
        File file = new File();
        file.setName("test.txt");
        when(fileRepository.findById(1)).thenReturn(Optional.of(file));

        String name = fileStorageService.getFileName(1);
        assertEquals("test.txt", name);
    }

    @Test
    void getFileContent_InvalidId_ThrowsException() {
        when(fileRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(IOException.class, () -> fileStorageService.getFileContent(1));
    }

    @Test
    void getFileContent_ValidId_ReturnsContent() throws IOException {
        File file = new File();
        file.setLocation(tempDir.resolve("test.txt").toString());
        Files.write(tempDir.resolve("test.txt"), "content".getBytes());
        
        when(fileRepository.findById(1)).thenReturn(Optional.of(file));

        byte[] content = fileStorageService.getFileContent(1);
        assertArrayEquals("content".getBytes(), content);
    }
}