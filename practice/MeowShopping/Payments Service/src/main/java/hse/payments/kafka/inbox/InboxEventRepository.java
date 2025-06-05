package hse.payments.kafka.inbox;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import hse.payments.kafka.events.InboxEvent;

public interface InboxEventRepository extends JpaRepository<InboxEvent, Long> {
    List<InboxEvent> findAllByProcessedFalseOrderByCreatedAtAsc();
}