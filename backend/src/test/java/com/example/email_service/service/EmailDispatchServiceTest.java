package com.example.email_service.service;

import com.example.email_service.dto.EmailQueueMessage;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import com.sendgrid.*;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailDispatchServiceTest {

    @Mock
    private SendGrid sendGrid;

    @Mock
    private EmailRepository emailRepository;

    private EmailDispatchService service; // will be spy for sendEmailDirectly

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("Starting EmailDispatchServiceTest");

        // normal instance for sendEmail tests
        service = new EmailDispatchService(sendGrid, emailRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println("Finished EmailDispatchServiceTest");
    }


    // sendEmail success path
    @Test
    void sendEmail_success_setsSentStatus() throws Exception {
        EmailNotification email = new EmailNotification();
        email.setStatus(DeliveryStatus.QUEUED);

        when(emailRepository.findById(any()))
                .thenReturn(Optional.of(email));

        Response response = new Response();
        response.setStatusCode(202);
        response.setHeaders(Map.of("X-Message-Id", "sg-123"));

        when(sendGrid.api(any())).thenReturn(response);

        EmailQueueMessage msg = new EmailQueueMessage();
        msg.setEmailId(UUID.randomUUID());
        msg.setFrom("from@test.com");
        msg.setTo("to@test.com");
        msg.setSubject("Test");
        msg.setBody("Hello");

        service.sendEmail(msg);

        assertEquals(DeliveryStatus.SENT, email.getStatus());
        assertEquals("sg-123", email.getProviderTrackingId());
        verify(emailRepository).save(email);
    }

    // sendEmail failure path (non-202)
    @Test
    void sendEmail_failure_setsFailedStatus() throws Exception {
        EmailNotification email = new EmailNotification();

        when(emailRepository.findById(any()))
                .thenReturn(Optional.of(email));

        Response response = new Response();
        response.setStatusCode(400);

        when(sendGrid.api(any())).thenReturn(response);

        EmailQueueMessage msg = new EmailQueueMessage();
        msg.setEmailId(UUID.randomUUID());

        service.sendEmail(msg);

        assertEquals(DeliveryStatus.FAILED, email.getStatus());
        verify(emailRepository).save(email);
    }

    // sendEmail exception path
    @Test
    void sendEmail_exception_setsFailedStatus() throws Exception {
        EmailNotification email = new EmailNotification();

        when(emailRepository.findById(any()))
                .thenReturn(Optional.of(email));

        when(sendGrid.api(any()))
                .thenThrow(new RuntimeException("SendGrid down"));

        EmailQueueMessage msg = new EmailQueueMessage();
        msg.setEmailId(UUID.randomUUID());

        service.sendEmail(msg);

        assertEquals(DeliveryStatus.FAILED, email.getStatus());
        verify(emailRepository).save(email);
    }

    // sendEmailDirectly success path
    @Test
    void sendEmailDirectly_success_returnsMessageId() throws Exception {
        EmailDispatchService spyService = spy(service);

        doReturn("mock-msg-123")
                .when(spyService)
                .sendEmailDirectly(anyString(), anyString(), anyString(), anyString());

        String messageId = spyService.sendEmailDirectly("from@test.com", "to@test.com", "Subj", "Body");

        assertEquals("mock-msg-123", messageId);
    }

    // sendEmailDirectly throws exception
    @Test
    void sendEmailDirectly_exception_throwsRuntime() throws Exception {
        // ⚡ Spy the service to stub sendEmailDirectly
        EmailDispatchService spyService = spy(service);

        doThrow(new RuntimeException("SendGrid down"))
                .when(spyService)
                .sendEmailDirectly(anyString(), anyString(), anyString(), anyString());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                spyService.sendEmailDirectly("from@test.com", "to@test.com", "Subj", "Body")
        );

        assertEquals("SendGrid down", ex.getMessage());
    }

    // sendEmailDirectly non-2xx response path
    @Test
    void sendEmailDirectly_failure_throwsException() throws IOException {
        SendGrid mockSendGrid = mock(SendGrid.class);
        Response response = new Response();
        response.setStatusCode(400);
        response.setBody("{\"errors\":[{\"message\":\"Invalid API key\"}]}");

        when(mockSendGrid.api(any())).thenReturn(response);

        EmailDispatchService testService = new EmailDispatchService(mockSendGrid, emailRepository);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                testService.sendEmailDirectly("from@test.com", "to@test.com", "Subj", "Body")
        );

        assertTrue(ex.getMessage().contains("SendGrid returned error"));
    }
}