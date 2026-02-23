package com.example.email_service.service;

import com.example.email_service.model.entity.EmailDeliveryHistory;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.DeliveryHistoryRepository;
import com.example.email_service.repository.EmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WebhookServiceTest {

    private EmailRepository emailRepository;
    private DeliveryHistoryRepository historyRepository;
    private WebhookService webhookService;

    @BeforeEach
    void setUp() {
        emailRepository = mock(EmailRepository.class);
        historyRepository = mock(DeliveryHistoryRepository.class);
        webhookService = new WebhookService(historyRepository, emailRepository);
    }

    @Test
    void handleStatusUpdate_emailExists_updatesEmailAndSavesHistory() {
        // Arrange
        String providerId = "abc-123";
        String recipient = "test@example.com";
        EmailNotification email = new EmailNotification();
        email.setId(UUID.randomUUID());
        email.setStatus(DeliveryStatus.CREATED);

        when(emailRepository.findByProviderTrackingId(providerId)).thenReturn(Optional.of(email));

        // Act
        webhookService.handleStatusUpdate(providerId, DeliveryStatus.SENT, recipient);

        // Assert email status updated
        ArgumentCaptor<EmailNotification> emailCaptor = ArgumentCaptor.forClass(EmailNotification.class);
        verify(emailRepository).save(emailCaptor.capture());
        assertEquals(DeliveryStatus.SENT, emailCaptor.getValue().getStatus());

        // Assert history saved
        ArgumentCaptor<EmailDeliveryHistory> historyCaptor = ArgumentCaptor.forClass(EmailDeliveryHistory.class);
        verify(historyRepository).save(historyCaptor.capture());
        EmailDeliveryHistory history = historyCaptor.getValue();
        assertEquals(email.getId(), history.getEmailId());
        assertEquals(DeliveryStatus.SENT, history.getStatus());
        assertTrue(history.getProviderMetadata().contains(recipient));
        assertNotNull(history.getRecordedAt());
    }

    @Test
    void handleStatusUpdate_emailDoesNotExist_logsWarning() {
        // Arrange
        String providerId = "missing-id";
        String recipient = "test@example.com";
        when(emailRepository.findByProviderTrackingId(providerId)).thenReturn(Optional.empty());

        // Redirect system out to capture log
        java.io.ByteArrayOutputStream outContent = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(outContent));

        // Act
        webhookService.handleStatusUpdate(providerId, DeliveryStatus.SENT, recipient);

        // Assert log output
        String log = outContent.toString();
        assertTrue(log.contains("WARNING: Webhook event received for unknown SendGrid ID: " + providerId));
    }
}