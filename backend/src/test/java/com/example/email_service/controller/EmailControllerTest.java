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
import java.util.List;
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

    //send success mail
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
        savedEmail.setStatus(DeliveryStatus.CREATED);

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


    // send - failure case
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


    // get - success
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
        email.setStatus(DeliveryStatus.SENT);
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


    // get - not found
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


    // delete - success
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


    // delete - not found
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

    //getall - without status
    @Test
    void getAllEmails_noStatus_returnsAll() {
        System.out.println("TEST: getAllEmails_noStatus START");

        EmailNotification email1 = new EmailNotification();
        email1.setId(UUID.randomUUID());
        email1.setRecipientEmail("to1@test.com");
        email1.setSenderEmail("from1@test.com");
        email1.setFinalSubject("Sub1");
        email1.setFinalBody("Body1");
        email1.setStatus(DeliveryStatus.SENT);
        email1.setCreatedAt(LocalDateTime.now());

        EmailNotification email2 = new EmailNotification();
        email2.setId(UUID.randomUUID());
        email2.setRecipientEmail("to2@test.com");
        email2.setSenderEmail("from2@test.com");
        email2.setFinalSubject("Sub2");
        email2.setFinalBody("Body2");
        email2.setStatus(DeliveryStatus.FAILED);
        email2.setCreatedAt(LocalDateTime.now());

        when(emailRepository.filterEmails(null, null, null, null))
                .thenReturn(List.of(email1, email2));

        var response = emailController.getAllEmails(null);

        System.out.println("Response count: " + response.getBody().size());

        assertEquals(2, response.getBody().size());
        assertEquals("to1@test.com", response.getBody().get(0).getRecipientEmail());
        assertEquals("to2@test.com", response.getBody().get(1).getRecipientEmail());

        verify(emailRepository, times(1))
                .filterEmails(null, null, null, null);

        System.out.println("TEST: getAllEmails_noStatus END\n");
    }

    //get all - with status
    @Test
    void getAllEmails_withStatus_returnsFiltered() {
        System.out.println("TEST: getAllEmails_withStatus START");

        DeliveryStatus status = DeliveryStatus.SENT;

        EmailNotification email = new EmailNotification();
        email.setId(UUID.randomUUID());
        email.setRecipientEmail("to@test.com");
        email.setSenderEmail("from@test.com");
        email.setFinalSubject("Sub");
        email.setFinalBody("Body");
        email.setStatus(status);
        email.setCreatedAt(LocalDateTime.now());

        when(emailRepository.filterEmails(status, null, null, null))
                .thenReturn(List.of(email));

        var response = emailController.getAllEmails("SENT");

        System.out.println("Response count: " + response.getBody().size());

        assertEquals(1, response.getBody().size());
        assertEquals("SENT", response.getBody().get(0).getDeliveryStatus());

        verify(emailRepository, times(1))
                .filterEmails(status, null, null, null);

        System.out.println("TEST: getAllEmails_withStatus END\n");
    }
}
