package com.example.email_service.service;

import com.example.email_service.model.entity.EmailDeliveryHistory;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.DeliveryHistoryRepository;
import com.example.email_service.repository.EmailRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class WebhookService {

    private final DeliveryHistoryRepository historyRepository;
    private final EmailRepository emailRepository;

    public WebhookService(DeliveryHistoryRepository historyRepository,
                          EmailRepository emailRepository) {
        this.historyRepository = historyRepository;
        this.emailRepository = emailRepository;
    }

    public void handleStatusUpdate(String providerTrackingId, DeliveryStatus status, String recipientEmail) {
        Optional<EmailNotification> emailOpt = emailRepository.findByProviderTrackingId(providerTrackingId);

        if (emailOpt.isPresent()) {
            EmailNotification email = emailOpt.get();

            // Update status in EmailNotification table
            email.setStatus(status);
            emailRepository.save(email);

            // Add entry in EmailDeliveryHistory
            EmailDeliveryHistory history = new EmailDeliveryHistory();
            history.setEmailId(email.getId());
            history.setStatus(status);
            history.setRecordedAt(LocalDateTime.now());
            history.setProviderMetadata("{\"event\":\"" + status + "\",\"recipient\":\"" + recipientEmail + "\"}");

            historyRepository.save(history);
        } else {
            // Just log a warning, don’t throw exception
            System.out.println("WARNING: Webhook event received for unknown SendGrid ID: " + providerTrackingId);
        }
    }
}