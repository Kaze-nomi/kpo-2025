package hse.kpo.service;

import hse.kpo.config.s3.S3ConfigProperties;
import hse.kpo.entities.ReportMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3ConfigProperties s3Config;

    @Async
    public CompletableFuture<Void> uploadReportToS3(ReportMetadata reportMetadata, String reportContent) {
        return CompletableFuture.runAsync(() -> {
            try {
                byte[] contentBytes = reportContent.getBytes();
                String key = generateKey(reportMetadata);

                Map<String, String> metadata = new HashMap<>();
                metadata.put("title", reportMetadata.getTitle());
                metadata.put("sourceService", reportMetadata.getSourceService());
                metadata.put("reportType", reportMetadata.getReportType());
                metadata.put("timestamp", reportMetadata.getTimestamp().toString());
                metadata.put("compressed", String.valueOf(s3Config.isCompress()));
                metadata.put("encrypted", String.valueOf(s3Config.isEncrypt()));

                if (contentBytes.length > 1_000_000) {
                    uploadSharded(contentBytes, key, metadata);
                } else {
                    uploadSingle(contentBytes, key, metadata);
                }
                log.info("Report uploaded to S3: {}", key);
            } catch (S3Exception e) {
                log.error("S3 upload failed", e);
            }
        });
    }

    private void uploadSingle(byte[] content, String key, Map<String, String> metadata) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(s3Config.getBucket())
                        .key(key)
                        .metadata(metadata)
                        .build(),
                RequestBody.fromBytes(content)
        );
    }

    private void uploadSharded(byte[] content, String baseKey, Map<String, String> metadata) {
        int partSize = 500_000; // 500KB per shard
        int partCount = (int) Math.ceil((double) content.length / partSize);

        for (int i = 0; i < partCount; i++) {
            int start = i * partSize;
            int end = Math.min((i + 1) * partSize, content.length);
            byte[] part = Arrays.copyOfRange(content, start, end);

            String partKey = baseKey + "_part" + (i + 1);
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(s3Config.getBucket())
                            .key(partKey)
                            .metadata(metadata)
                            .build(),
                    RequestBody.fromBytes(part)
            );
        }
    }

    private String generateKey(ReportMetadata metadata) {
        return String.format("reports/%s/%s/%s",
                metadata.getReportType(),
                metadata.getTimestamp().toLocalDate(),
                metadata.getTitle().replace(" ", "_"));
    }
}