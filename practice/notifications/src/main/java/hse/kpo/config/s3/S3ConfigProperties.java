package hse.kpo.config.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "s3")
public class S3ConfigProperties {
    private String endpoint;
    private String bucket;
    private boolean compress;
    private boolean encrypt;
}