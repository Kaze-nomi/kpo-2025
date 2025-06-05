package hse.kpo.entities;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportMetadata {
    private String title;
    private String sourceService;
    private String reportType;
    private LocalDateTime timestamp;
    private boolean compressed;
    private boolean encrypted;
}