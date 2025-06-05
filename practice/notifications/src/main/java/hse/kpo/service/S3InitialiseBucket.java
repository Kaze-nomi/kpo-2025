package hse.kpo.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import hse.kpo.config.s3.S3ConfigProperties;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.s3.S3Client;

@RequiredArgsConstructor
@Component
public class S3InitialiseBucket implements CommandLineRunner{

    private final S3Client s3Client;

    private final S3ConfigProperties properties;
    
    @Override
    public void run(String... args) throws Exception {
        var bucketName = properties.getBucket();
        if (bucketName != null && !s3Client.listBuckets().buckets().stream().anyMatch(b -> b.name().equals(bucketName))) {
            s3Client.createBucket(builder -> builder.bucket(bucketName));
        }
    }

}
