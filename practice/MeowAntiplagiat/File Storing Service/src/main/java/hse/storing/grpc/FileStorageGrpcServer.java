package hse.storing.grpc;

import hse.storing.services.FileStorageService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import hse.storing.proto.grpc.*;

import com.google.protobuf.ByteString;

@GrpcService
public class FileStorageGrpcServer extends FileStorageServiceGrpc.FileStorageServiceImplBase {

    private final FileStorageService fileStorageService;

    @Autowired
    public FileStorageGrpcServer(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void uploadFile(UploadFileRequest request,
            StreamObserver<UploadFileResponse> responseObserver) {
        try {
            String fileId = fileStorageService.saveFile(
                    request.getFileName(),
                    request.getFileContent().toByteArray());

            responseObserver.onNext(UploadFileResponse.newBuilder()
                    .setFileId(String.valueOf(fileId))
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }

    @Override
    public void getFile(GetFileRequest request,
            StreamObserver<GetFileResponse> responseObserver) {
        try {
            if (!request.getFileId().matches("\\d+")) {
                throw new IllegalArgumentException("ID файла должен быть числом!");
            }

            int fileId = Integer.parseInt(request.getFileId());
            byte[] content = fileStorageService.getFileContent(fileId);
            String fileName = fileStorageService.getFileName(fileId);

            responseObserver.onNext(GetFileResponse.newBuilder()
                    .setFileName(fileName)
                    .setFileContent(ByteString.copyFrom(content))
                    .build());
            responseObserver.onCompleted();

        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                    .withDescription(e.getMessage())
                    .withCause(e)
                    .asRuntimeException());
        }
    }
}