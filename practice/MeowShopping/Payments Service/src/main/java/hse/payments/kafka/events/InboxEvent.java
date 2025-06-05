package hse.payments.kafka.events;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "inbox_events")
@Getter
@Setter
@NoArgsConstructor
public class InboxEvent {

    @Id
    private Long id;

    private String eventType;

    private String payload;

    private boolean processed = false;

    private LocalDateTime createdAt;
    
}