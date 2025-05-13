package hse.analysis.grpc;

import hse.analysis.proto.grpc.*;
import hse.analysis.proto.grpc.FileStorageServiceGrpc.FileStorageServiceBlockingStub;
import hse.analysis.services.FileAnalysisService;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import net.devh.boot.grpc.client.inject.GrpcClient;

@GrpcService
@Slf4j
public class FileAnalysisGrpcServer extends FileAnalysisServiceGrpc.FileAnalysisServiceImplBase {

    private final FileAnalysisService fileAnalysisService;

    @GrpcClient("file-storage-service")
    @Setter
    private FileStorageServiceBlockingStub fileStorageClient;

    @Scheduled(fixedRate = 5000)
    private void checkConnections() {
        tryReconnect(fileStorageClient.getChannel());
    }

    private <T> void tryReconnect(T channel) {
        ManagedChannel c = (ManagedChannel) channel;
        if (c.getState(true) != ConnectivityState.READY) {
            c.resetConnectBackoff();
            c.enterIdle();
            
            fileStorageClient = FileStorageServiceGrpc.newBlockingStub(fileStorageClient.getChannel());
        }
    }

    @Autowired
    public FileAnalysisGrpcServer(FileAnalysisService fileAnalysisService) {
        this.fileAnalysisService = fileAnalysisService;
    }

    @Override
    public void analyzeFile(AnalyzeFileRequest request, StreamObserver<AnalyzeFileResponse> responseObserver) {

        try {
            if (!request.getFileId().matches("\\d+")) {
                throw new IllegalArgumentException("ID файла должен быть числом!");
            }
        } catch (IllegalArgumentException e) {
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException());
            return;
        }

        var analyzedFiel = fileAnalysisService.getFile(request.getFileId());

        if (analyzedFiel != null) {
            AnalyzeFileResponse response = AnalyzeFileResponse.newBuilder()
                    .setAnalysisResult(analyzedFiel.getResult())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            return;
        }

        String fileId = request.getFileId();

        try {
            GetFileRequest getFileRequest = GetFileRequest.newBuilder().setFileId(fileId).build();

            GetFileResponse response_con = fileStorageClient.getFile(getFileRequest);
            String fileContent = response_con.getFileContent().toStringUtf8();
            String fileName = response_con.getFileName();

            String result = fileAnalysisService.analyzeFile(fileId, fileName, fileContent);

            AnalyzeFileResponse response = AnalyzeFileResponse.newBuilder()
                    .setAnalysisResult(result)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                responseObserver.onError(
                        Status.FAILED_PRECONDITION
                                .withDescription("Сервис storage сейчас спит, попробуй позже! 💤")
                                .asRuntimeException());
            } else {
                responseObserver.onError(
                        Status.INTERNAL
                                .withDescription("Ошибка при общении с storage: " + e.getMessage())
                                .asRuntimeException());
            }
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Ошибка: " + e.getMessage())
                            .asRuntimeException());
        }
    }

    @Override
    public void getWordCloud(AnalyzeFileRequest request, StreamObserver<WordCloudResponse> responseObserver) {
        try {
            byte[] wordCloud = fileAnalysisService.getWordCloud(request.getFileId());
            WordCloudResponse response = WordCloudResponse.newBuilder()
                    .setWordCloud(com.google.protobuf.ByteString.copyFrom(wordCloud))
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Ошибка: " + e.getMessage())
                            .asRuntimeException());
        }
    }
}