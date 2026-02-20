package com.example.email_service.messaging;

import com.example.email_service.config.RabbitMQConfig;
import com.example.email_service.dto.SendGridEventDto;
import com.example.email_service.model.entity.EmailDeliveryHistory;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.DeliveryHistoryRepository;
import com.example.email_service.repository.EmailRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class SendGridEventConsumer {

    private final EmailRepository emailRepository;
    private final DeliveryHistoryRepository historyRepository;

    public SendGridEventConsumer(EmailRepository emailRepository,
                                 DeliveryHistoryRepository historyRepository) {
        this.emailRepository = emailRepository;
        this.historyRepository = historyRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_STATUS_QUEUE)
    public void consume(SendGridEventDto event) {

        EmailNotification email = emailRepository
                .findByProviderTrackingId(event.getSg_message_id())
                .orElseThrow(() -> new RuntimeException(
                        "Email not found for SendGrid ID: " + event.getSg_message_id()
                ));

        DeliveryStatus status = mapEvent(event.getEvent());
        email.setDeliveryStatus(status);
        emailRepository.save(email);

        EmailDeliveryHistory history = new EmailDeliveryHistory();
        history.setEmailId(email.getId());
        history.setStatus(status);
        history.setRecordedAt(LocalDateTime.now());
        history.setProviderMetadata(toJson(event));

        historyRepository.save(history);
    }

    private DeliveryStatus mapEvent(String event) {
        return switch (event.toLowerCase()) {
            case "sent", "delivered" -> DeliveryStatus.SENT;
            case "open", "opened" -> DeliveryStatus.OPENED;
            case "failed", "bounce", "dropped" -> DeliveryStatus.FAILED;
            default -> DeliveryStatus.QUEUED;
        };
    }

    private String toJson(SendGridEventDto event) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(event);
        } catch (Exception e) {
            return "{}";
        }
    }
}
