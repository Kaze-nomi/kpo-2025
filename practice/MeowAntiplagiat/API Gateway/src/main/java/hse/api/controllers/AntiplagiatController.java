package hse.api.controllers;

import com.google.protobuf.ByteString;
import hse.api.grpc.FileStorageServiceGrpc.FileStorageServiceBlockingStub;
import hse.api.grpc.AnalyzeFileRequest;
import hse.api.grpc.AnalyzeFileResponse;
import hse.api.grpc.FileAnalysisServiceGrpc;
import hse.api.grpc.FileAnalysisServiceGrpc.FileAnalysisServiceBlockingStub;
import hse.api.grpc.FileStorageServiceGrpc;
import hse.api.grpc.GetFileRequest;
import hse.api.grpc.GetFileResponse;
import hse.api.grpc.UploadFileRequest;
import hse.api.grpc.UploadFileResponse;
import hse.api.grpc.WordCloudResponse;
import io.grpc.StatusRuntimeException;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestController
@RequestMapping("/antiplagiat")
@RequiredArgsConstructor
@Tag(name = "Антиплагиат", description = "Сервис для работы с файлами")
public class AntiplagiatController {

    @GrpcClient("file-storing-service")
    private FileStorageServiceBlockingStub fileStorageClient;

    @GrpcClient("file-analysis-service")
    private FileAnalysisServiceBlockingStub fileAnalysisClient;

    @Scheduled(fixedRate = 5000)
    private void checkConnections() {
        tryReconnect(fileStorageClient.getChannel());
        tryReconnect(fileAnalysisClient.getChannel());
    }

    private <T> void tryReconnect(T channel) {
        ManagedChannel c = (ManagedChannel) channel;
        if (c.getState(true) != ConnectivityState.READY) {
            c.resetConnectBackoff();
            c.enterIdle();

            if (channel == fileStorageClient.getChannel()) {
                fileStorageClient = FileStorageServiceGrpc.newBlockingStub(fileStorageClient.getChannel());

            }

            if (channel == fileAnalysisClient.getChannel()) {
                fileAnalysisClient = FileAnalysisServiceGrpc.newBlockingStub(fileAnalysisClient.getChannel());
            }
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Загрузка текстового файла", description = "Загрузка .txt файла для хранения и проверки на антиплагиат", requestBody = @RequestBody(content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE, schema = @Schema(implementation = FileUploadSchema.class))))
    public ResponseEntity<String> uploadFile(
            @Parameter(description = "Текстовый файл для загрузки", required = true, content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)) @RequestParam("file") MultipartFile file) {

        if (!Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith(".txt")) {
            return ResponseEntity.badRequest().body("Разрешены только .txt файлы");
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Файл не может быть пустым!");
        }

        try {
            UploadFileRequest request = UploadFileRequest.newBuilder()
                    .setFileName(file.getOriginalFilename())
                    .setFileContent(ByteString.copyFrom(file.getBytes()))
                    .build();

            UploadFileResponse response = fileStorageClient.uploadFile(request);
            return ResponseEntity.ok(response.getFileId());

        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                return ResponseEntity.status(503)
                        .body("Сервис storage сейчас спит, попробуй позже! 💤");
            }
            return ResponseEntity.internalServerError()
                    .body("Ошибка при общении с storage: " + e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка загрузки файла: " + e.getMessage());
        }
    }

    @GetMapping("/storing/{id}")
    @Operation(summary = "Получить содержимое файла")
    public ResponseEntity<String> getFileContent(@PathVariable String id) {
        try {
            GetFileResponse response = fileStorageClient.getFile(GetFileRequest.newBuilder().setFileId(id).build());
            return ResponseEntity.ok("Содержимое файла:" + "\n" + response.getFileContent().toStringUtf8());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                return ResponseEntity.status(503)
                        .body("Сервис storage сейчас спит, попробуй позже! 💤");
            }
            return ResponseEntity.internalServerError().body("Ошибка получения файла: " + e.getMessage());
        }
    }

    @GetMapping("/analysis/{id}")
    @Operation(summary = "Провести анализ файла")
    public ResponseEntity<String> getFileAnalysis(@PathVariable String id) {
        try {
            AnalyzeFileResponse response = fileAnalysisClient
                    .analyzeFile(AnalyzeFileRequest.newBuilder().setFileId(id).build());
            return ResponseEntity.ok("Результат анализа:" + "\n" + response.getAnalysisResult());
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                return ResponseEntity.status(503)
                        .body("Сервис analysis сейчас спит, попробуй позже! 💤");
            }
            return ResponseEntity.internalServerError().body("Ошибка проведения анализа: " + e.getMessage());
        }
    }

    @GetMapping("/analysis/wordcloud/{fileId}")
    @Operation(summary = "Получить облако слов")
    public ResponseEntity<?> getWordCloudImage(@PathVariable String fileId) {
        try {
            WordCloudResponse response = fileAnalysisClient.getWordCloud(
                    AnalyzeFileRequest.newBuilder().setFileId(fileId).build());

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(response.getWordCloud().toByteArray());

        } catch (StatusRuntimeException e) {
            String errorMessage;
            HttpStatus status;

            if (e.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                errorMessage = "Сервис analysis сейчас спит, попробуй позже! 💤";
                status = HttpStatus.SERVICE_UNAVAILABLE;
            } else {
                errorMessage = "Ошибка получения облака слов: " + e.getMessage();
                status = HttpStatus.INTERNAL_SERVER_ERROR;
            }

            return ResponseEntity.status(status)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(errorMessage.getBytes(StandardCharsets.UTF_8));
        }
    }

    @Schema(name = "FileUploadSchema")
    static class FileUploadSchema {
        @Schema(format = "binary", description = "Файл для загрузки")
        public String file;
    }
}