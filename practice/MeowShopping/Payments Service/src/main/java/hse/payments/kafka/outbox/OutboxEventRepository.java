package hse.payments.kafka.outbox;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import hse.payments.kafka.events.OutboxEvent;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findAllBySentFalseOrderByCreatedAtAsc();
}