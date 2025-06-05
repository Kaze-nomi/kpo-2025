package hse.storing.GrpcTests;

import hse.storing.grpc.FileStorageGrpcServer;
import hse.storing.proto.grpc.*;
import hse.storing.services.FileStorageService;
import io.grpc.stub.StreamObserver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageGrpcServerMockTest {

    @Mock
    private FileStorageService fileStorageService;
    
    @Mock
    private StreamObserver<UploadFileResponse> uploadResponseObserver;
    
    @Mock
    private StreamObserver<GetFileResponse> getFileResponseObserver;

    @Captor
    private ArgumentCaptor<UploadFileResponse> uploadResponseCaptor;

    @Captor
    private ArgumentCaptor<GetFileResponse> getFileResponseCaptor;

    @Captor
    private ArgumentCaptor<Throwable> errorCaptor;

    @InjectMocks
    private FileStorageGrpcServer grpcServer;

    @Test
    public void uploadFile_Success_SendsResponseWithFileId() throws Exception {
        when(fileStorageService.saveFile(any(), any()))
            .thenReturn("ID файла: 123");
        
        UploadFileRequest request = UploadFileRequest.newBuilder()
            .setFileName("test.txt")
            .setFileContent(com.google.protobuf.ByteString.copyFromUtf8("content"))
            .build();

        grpcServer.uploadFile(request, uploadResponseObserver);

        verify(uploadResponseObserver).onNext(uploadResponseCaptor.capture());
        verify(uploadResponseObserver).onCompleted();
        
        UploadFileResponse response = uploadResponseCaptor.getValue();
        assertTrue(response.getFileId().contains("123"));
    }

    @Test
    public void uploadFile_Error_SendsErrorResponse() throws Exception {
        when(fileStorageService.saveFile(any(), any()))
            .thenThrow(new IOException("Test error"));
        
        UploadFileRequest request = UploadFileRequest.newBuilder()
            .setFileName("test.txt")
            .setFileContent(com.google.protobuf.ByteString.EMPTY)
            .build();

        grpcServer.uploadFile(request, uploadResponseObserver);

        verify(uploadResponseObserver).onError(errorCaptor.capture());
        
        Throwable error = errorCaptor.getValue();
        assertEquals("INTERNAL: Test error", error.getMessage());
    }

    @Test
    public void getFile_Success_SendsFileContent() throws Exception {
        when(fileStorageService.getFileName(123))
            .thenReturn("test.txt");
        when(fileStorageService.getFileContent(123))
            .thenReturn("file content".getBytes());
        
        GetFileRequest request = GetFileRequest.newBuilder()
            .setFileId("123")
            .build();

        grpcServer.getFile(request, getFileResponseObserver);

        verify(getFileResponseObserver).onNext(getFileResponseCaptor.capture());
        verify(getFileResponseObserver).onCompleted();
        
        GetFileResponse response = getFileResponseCaptor.getValue();
        assertEquals("test.txt", response.getFileName());
        assertArrayEquals("file content".getBytes(), response.getFileContent().toByteArray());
    }

    @Test
    public void getFile_InvalidId_SendsError() throws Exception {
        when(fileStorageService.getFileName(anyInt()))
            .thenThrow(new IOException("File not found"));
        
        GetFileRequest request = GetFileRequest.newBuilder()
            .setFileId("999")
            .build();

        grpcServer.getFile(request, getFileResponseObserver);

        verify(getFileResponseObserver).onError(errorCaptor.capture());
        
        Throwable error = errorCaptor.getValue();
        assertTrue(error.getMessage().contains("File not found"));
    }

    @Test
    public void getFile_InvalidIdFormat_SendsError() {
        GetFileRequest request = GetFileRequest.newBuilder()
            .setFileId("invalid")
            .build();

        grpcServer.getFile(request, getFileResponseObserver);

        verify(getFileResponseObserver).onError(errorCaptor.capture());
        
        Throwable error = errorCaptor.getValue();
        assertTrue(error.getMessage().contains("ID файла должен быть числом!"));
    }
}

