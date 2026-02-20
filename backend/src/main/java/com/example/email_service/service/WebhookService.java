package com.example.email_service.service;

import com.example.email_service.model.entity.EmailDeliveryHistory;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.DeliveryHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class WebhookService {

    private final DeliveryHistoryRepository repository;

    public WebhookService(DeliveryHistoryRepository repository) {
        this.repository = repository;
    }

    public void handleStatusUpdate(UUID emailId, DeliveryStatus status) {
        EmailDeliveryHistory history = new EmailDeliveryHistory();
        history.setEmailId(emailId);
        history.setStatus(status);
        history.setRecordedAt(LocalDateTime.now());
        repository.save(history);
    }
}
