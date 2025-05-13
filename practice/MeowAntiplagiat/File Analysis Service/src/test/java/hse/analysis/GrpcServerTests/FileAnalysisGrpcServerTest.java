package hse.analysis.GrpcServerTests;

import hse.analysis.domains.Analysis;
import hse.analysis.grpc.FileAnalysisGrpcServer;
import hse.analysis.proto.grpc.*;
import hse.analysis.services.FileAnalysisService;
import hse.analysis.proto.grpc.FileStorageServiceGrpc.FileStorageServiceBlockingStub;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;

public class FileAnalysisGrpcServerTest {

    @Mock
    private FileAnalysisService fileAnalysisService;
    
    @Mock
    private FileStorageServiceBlockingStub fileStorageClient;
    
    @Mock
    private StreamObserver<AnalyzeFileResponse> analyzeFileResponseObserver;
    
    @Mock
    private StreamObserver<WordCloudResponse> wordCloudResponseObserver;
    
    private FileAnalysisGrpcServer fileAnalysisGrpcServer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        fileAnalysisGrpcServer = new FileAnalysisGrpcServer(fileAnalysisService);
        fileAnalysisGrpcServer.setFileStorageClient(fileStorageClient);
    }

    @Test
    public void testAnalyzeFile_InvalidFileId() {
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId("abc")
                .build();

        fileAnalysisGrpcServer.analyzeFile(request, analyzeFileResponseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(analyzeFileResponseObserver).onError(throwableCaptor.capture());
        
        StatusRuntimeException exception = (StatusRuntimeException) throwableCaptor.getValue();
        assertEquals(Status.INVALID_ARGUMENT.getCode(), exception.getStatus().getCode());
        assertEquals("ID файла должен быть числом!", exception.getStatus().getDescription());
    }

    @Test
    public void testAnalyzeFile_ExistingAnalysis() {
        String fileId = "123";
        String analysisResult = "Test analysis result";
        
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId(fileId)
                .build();
        
        Analysis existingAnalysis = new Analysis();
        existingAnalysis.setResult(analysisResult);
        
        when(fileAnalysisService.getFile(fileId)).thenReturn(existingAnalysis);

        fileAnalysisGrpcServer.analyzeFile(request, analyzeFileResponseObserver);

        ArgumentCaptor<AnalyzeFileResponse> responseCaptor = ArgumentCaptor.forClass(AnalyzeFileResponse.class);
        verify(analyzeFileResponseObserver).onNext(responseCaptor.capture());
        verify(analyzeFileResponseObserver).onCompleted();
        
        assertEquals(analysisResult, responseCaptor.getValue().getAnalysisResult());
    }

    @Test
    public void testAnalyzeFile_NewAnalysis() throws Exception {
        String fileId = "123";
        String fileName = "test.txt";
        String fileContent = "Test file content";
        String analysisResult = "Test analysis result";
        
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId(fileId)
                .build();
        
        GetFileResponse getFileResponse = GetFileResponse.newBuilder()
                .setFileName(fileName)
                .setFileContent(com.google.protobuf.ByteString.copyFromUtf8(fileContent))
                .build();
        
        when(fileAnalysisService.getFile(fileId)).thenReturn(null);
        when(fileStorageClient.getFile(any())).thenReturn(getFileResponse);
        when(fileAnalysisService.analyzeFile(fileId, fileName, fileContent)).thenReturn(analysisResult);

        fileAnalysisGrpcServer.analyzeFile(request, analyzeFileResponseObserver);

        ArgumentCaptor<AnalyzeFileResponse> responseCaptor = ArgumentCaptor.forClass(AnalyzeFileResponse.class);
        verify(analyzeFileResponseObserver).onNext(responseCaptor.capture());
        verify(analyzeFileResponseObserver).onCompleted();
        
        assertEquals(analysisResult, responseCaptor.getValue().getAnalysisResult());
    }

    @Test
    public void testAnalyzeFile_StorageUnavailable() {
        String fileId = "123";
        
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId(fileId)
                .build();
        
        when(fileAnalysisService.getFile(fileId)).thenReturn(null);
        when(fileStorageClient.getFile(any())).thenThrow(
                new StatusRuntimeException(Status.UNAVAILABLE));

        fileAnalysisGrpcServer.analyzeFile(request, analyzeFileResponseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(analyzeFileResponseObserver).onError(throwableCaptor.capture());
        
        StatusRuntimeException exception = (StatusRuntimeException) throwableCaptor.getValue();
        assertEquals(Status.FAILED_PRECONDITION.getCode(), exception.getStatus().getCode());
        assertEquals("Сервис storage сейчас спит, попробуй позже! 💤", exception.getStatus().getDescription());
    }

    @Test
    public void testAnalyzeFile_StorageError() {
        String fileId = "123";
        
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId(fileId)
                .build();
        
        when(fileAnalysisService.getFile(fileId)).thenReturn(null);
        when(fileStorageClient.getFile(any())).thenThrow(
                new StatusRuntimeException(Status.INTERNAL));

        fileAnalysisGrpcServer.analyzeFile(request, analyzeFileResponseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(analyzeFileResponseObserver).onError(throwableCaptor.capture());
        
        StatusRuntimeException exception = (StatusRuntimeException) throwableCaptor.getValue();
        assertEquals(Status.INTERNAL.getCode(), exception.getStatus().getCode());
        assertTrue(exception.getStatus().getDescription().startsWith("Ошибка при общении с storage:"));
    }

    @Test
    public void testAnalyzeFile_GeneralError() throws Exception {
        String fileId = "123";
        String fileName = "test.txt";
        String fileContent = "Test file content";
        
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId(fileId)
                .build();
        
        GetFileResponse getFileResponse = GetFileResponse.newBuilder()
                .setFileName(fileName)
                .setFileContent(com.google.protobuf.ByteString.copyFromUtf8(fileContent))
                .build();
        
        when(fileAnalysisService.getFile(fileId)).thenReturn(null);
        when(fileStorageClient.getFile(any())).thenReturn(getFileResponse);
        when(fileAnalysisService.analyzeFile(fileId, fileName, fileContent))
                .thenThrow(new RuntimeException("Test error"));

        fileAnalysisGrpcServer.analyzeFile(request, analyzeFileResponseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(analyzeFileResponseObserver).onError(throwableCaptor.capture());
        
        StatusRuntimeException exception = (StatusRuntimeException) throwableCaptor.getValue();
        assertEquals(Status.INTERNAL.getCode(), exception.getStatus().getCode());
        assertEquals("Ошибка: Test error", exception.getStatus().getDescription());
    }

    @Test
    public void testGetWordCloud_Success() throws Exception {
        String fileId = "123";
        byte[] wordCloudBytes = "test word cloud".getBytes();
        
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId(fileId)
                .build();
        
        Analysis analysis = new Analysis();
        when(fileAnalysisService.getFile(fileId)).thenReturn(analysis);
        when(fileAnalysisService.getWordCloud(fileId)).thenReturn(wordCloudBytes);

        fileAnalysisGrpcServer.getWordCloud(request, wordCloudResponseObserver);

        ArgumentCaptor<WordCloudResponse> responseCaptor = ArgumentCaptor.forClass(WordCloudResponse.class);
        verify(wordCloudResponseObserver).onNext(responseCaptor.capture());
        verify(wordCloudResponseObserver).onCompleted();
        
        assertArrayEquals(wordCloudBytes, responseCaptor.getValue().getWordCloud().toByteArray());
    }

    @Test
    public void testGetWordCloud_NoAnalysis() throws Exception {
        String fileId = "nonexistingfile";
        
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId(fileId)
                .build();
        
        when(fileAnalysisService.getWordCloud(fileId)).thenThrow(new IOException("Для получения облака слов нужно сначала провести анализ файла!"));

        fileAnalysisGrpcServer.getWordCloud(request, wordCloudResponseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(wordCloudResponseObserver).onError(throwableCaptor.capture());
        
        StatusRuntimeException exception = (StatusRuntimeException) throwableCaptor.getValue();
        assertEquals(Status.INTERNAL.getCode(), exception.getStatus().getCode());
        assertEquals("Ошибка: Для получения облака слов нужно сначала провести анализ файла!", 
                exception.getStatus().getDescription());
    }

    @Test
    public void testGetWordCloud_Error() throws Exception {
        String fileId = "123";
        String errorMessage = "Test error";
        
        AnalyzeFileRequest request = AnalyzeFileRequest.newBuilder()
                .setFileId(fileId)
                .build();
        
        Analysis analysis = new Analysis();
        when(fileAnalysisService.getFile(fileId)).thenReturn(analysis);
        when(fileAnalysisService.getWordCloud(fileId))
                .thenThrow(new IOException(errorMessage));

        fileAnalysisGrpcServer.getWordCloud(request, wordCloudResponseObserver);

        ArgumentCaptor<Throwable> throwableCaptor = ArgumentCaptor.forClass(Throwable.class);
        verify(wordCloudResponseObserver).onError(throwableCaptor.capture());
        
        StatusRuntimeException exception = (StatusRuntimeException) throwableCaptor.getValue();
        assertEquals(Status.INTERNAL.getCode(), exception.getStatus().getCode());
        assertEquals("Ошибка: " + errorMessage, exception.getStatus().getDescription());
    }
}