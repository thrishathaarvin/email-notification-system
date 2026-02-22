package com.example.email_service.messaging;

import com.example.email_service.config.RabbitMQConfig;
import com.example.email_service.dto.SendGridEventDto;
import com.example.email_service.model.entity.EmailDeliveryHistory;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.DeliveryHistoryRepository;
import com.example.email_service.repository.EmailRepository;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

//This represents a message consumer which process sendgrid events and populate delivery history table
@Component
public class SendGridEventConsumer {

    //Constructor injection
    private final EmailRepository emailRepository;
    private final DeliveryHistoryRepository historyRepository;
    public SendGridEventConsumer(EmailRepository emailRepository,
                                 DeliveryHistoryRepository historyRepository) {
        this.emailRepository = emailRepository;
        this.historyRepository = historyRepository;
    }

    //this gets triggered when a new sendgrid event arrives in queue
    @RabbitListener(queues = RabbitMQConfig.EMAIL_STATUS_QUEUE)
    public void consume(SendGridEventDto event) {

        //Mapping sendgrid event to an internal email record
        EmailNotification email = emailRepository
                .findByProviderTrackingId(event.getSg_message_id())
                .orElseThrow(() -> new RuntimeException(
                        "Email not found for SendGrid ID: " + event.getSg_message_id()
                ));

        //converting string event to enum, updates and saves in repo
        DeliveryStatus status = mapEvent(event.getEvent());
        email.setStatus(status);
        emailRepository.save(email);

        //now, this is created in delivery history table for audit purpose
        EmailDeliveryHistory history = new EmailDeliveryHistory();
        history.setEmailId(email.getId());
        history.setStatus(status);
        history.setRecordedAt(LocalDateTime.now());
        history.setProviderMetadata(toJson(event));

        historyRepository.save(history);
    }

    //Conversion of string to enum logic
    private DeliveryStatus mapEvent(String event) {
        return switch (event.toLowerCase()) {
            case "sent", "delivered" -> DeliveryStatus.SENT;
            case "open", "opened" -> DeliveryStatus.OPENED;
            case "failed", "bounce", "dropped" -> DeliveryStatus.FAILED;
            default -> DeliveryStatus.QUEUED;
        };
    }

    //converts dto to JSON for storage reasons
    private String toJson(SendGridEventDto event) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper()
                    .writeValueAsString(event);
        } catch (Exception e) {
            return "{}";
        }
    }
}
