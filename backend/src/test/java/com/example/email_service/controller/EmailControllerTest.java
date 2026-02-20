package com.example.email_service.controller;

import com.example.email_service.dto.EmailNotificationDto;
import com.example.email_service.dto.EmailRequestDto;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import com.example.email_service.service.EmailDispatchService;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailControllerTest {

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private EmailDispatchService dispatchService;

    @InjectMocks
    private EmailController emailController;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        System.out.println("---- SETUP START ----");
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("Mocks initialized");
        System.out.println("---- SETUP END ----\n");
    }

    @AfterEach
    void tearDown() throws Exception {
        System.out.println("---- TEARDOWN ----\n");
        closeable.close();
    }

    // ===============================
    // TEST 1: sendEmail SUCCESS CASE
    // ===============================
    @Test
    void sendEmail_success() throws IOException {
        System.out.println("TEST: sendEmail_success START");

        EmailRequestDto request = new EmailRequestDto();
        request.setFrom("from@test.com");
        request.setTo("to@test.com");
        request.setSubject("Hello");
        request.setBody("Test body");

        EmailNotification savedEmail = new EmailNotification();
        savedEmail.setId(UUID.randomUUID());
        savedEmail.setDeliveryStatus(DeliveryStatus.CREATED);

        when(emailRepository.save(any(EmailNotification.class)))
                .thenReturn(savedEmail);

        when(dispatchService.sendEmailDirectly(
                anyString(), anyString(), anyString(), anyString()))
                .thenReturn("provider-123");

        String response = emailController.sendEmail(request);

        System.out.println("Controller response: " + response);

        assertEquals("Email sent successfully", response);

        verify(emailRepository, times(2)).save(any(EmailNotification.class));
        verify(dispatchService, times(1)).sendEmailDirectly(
                "from@test.com", "to@test.com", "Hello", "Test body");

        System.out.println("TEST: sendEmail_success END\n");
    }

    // =================================
    // TEST 2: sendEmail FAILURE CASE
    // =================================
    @Test
    void sendEmail_failure() throws IOException {
        System.out.println("TEST: sendEmail_failure START");

        EmailRequestDto request = new EmailRequestDto();
        request.setFrom("from@test.com");
        request.setTo("to@test.com");
        request.setSubject("Fail");
        request.setBody("Body");

        EmailNotification savedEmail = new EmailNotification();
        savedEmail.setId(UUID.randomUUID());

        when(emailRepository.save(any(EmailNotification.class)))
                .thenReturn(savedEmail);

        when(dispatchService.sendEmailDirectly(
                anyString(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("SendGrid down"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailController.sendEmail(request)
        );

        System.out.println("Exception message: " + exception.getMessage());

        assertTrue(exception.getMessage().contains("Failed to send email"));

        verify(emailRepository, times(2)).save(any(EmailNotification.class));

        System.out.println("TEST: sendEmail_failure END\n");
    }

    // =================================
    // TEST 3: getEmailById SUCCESS
    // =================================
    @Test
    void getEmailById_success() {
        System.out.println("TEST: getEmailById_success START");

        UUID emailId = UUID.randomUUID();

        EmailNotification email = new EmailNotification();
        email.setId(emailId);
        email.setRecipientEmail("to@test.com");
        email.setSenderEmail("from@test.com");
        email.setFinalSubject("Subject");
        email.setFinalBody("Body");
        email.setDeliveryStatus(DeliveryStatus.SENT);
        email.setCreatedAt(LocalDateTime.now());

        when(emailRepository.findById(emailId))
                .thenReturn(Optional.of(email));

        ResponseEntity<EmailNotificationDto> response =
                emailController.getEmailById(emailId);

        System.out.println("Response status: " + response.getStatusCode());

        EmailNotificationDto dto = response.getBody();

        assertNotNull(dto);
        assertEquals("to@test.com", dto.getRecipientEmail());
        assertEquals("from@test.com", dto.getFromEmail());
        assertEquals("SENT", dto.getDeliveryStatus());

        System.out.println("TEST: getEmailById_success END\n");
    }

    // =================================
    // TEST 4: getEmailById NOT FOUND
    // =================================
    @Test
    void getEmailById_notFound() {
        System.out.println("TEST: getEmailById_notFound START");

        UUID emailId = UUID.randomUUID();

        when(emailRepository.findById(emailId))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> emailController.getEmailById(emailId)
        );

        System.out.println("Exception message: " + exception.getMessage());

        assertTrue(exception.getMessage().contains("Email not found"));

        System.out.println("TEST: getEmailById_notFound END\n");
    }

    // =================================
    // TEST 5: deleteEmail SUCCESS
    // =================================
    @Test
    void deleteEmail_success() {
        System.out.println("TEST: deleteEmail_success START");

        UUID emailId = UUID.randomUUID();

        when(emailRepository.existsById(emailId))
                .thenReturn(true);

        String response = emailController.deleteEmail(emailId);

        System.out.println("Controller response: " + response);

        verify(emailRepository, times(1)).deleteById(emailId);
        assertTrue(response.contains("deleted successfully"));

        System.out.println("TEST: deleteEmail_success END\n");
    }

    // =================================
    // TEST 6: deleteEmail NOT FOUND
    // =================================
    @Test
    void deleteEmail_notFound() {
        System.out.println("TEST: deleteEmail_notFound START");

        UUID emailId = UUID.randomUUID();

        when(emailRepository.existsById(emailId))
                .thenReturn(false);

        String response = emailController.deleteEmail(emailId);

        System.out.println("Controller response: " + response);

        verify(emailRepository, never()).deleteById(any());
        assertTrue(response.contains("not found"));

        System.out.println("TEST: deleteEmail_notFound END\n");
    }
}
