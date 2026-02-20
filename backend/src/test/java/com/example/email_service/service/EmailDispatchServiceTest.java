package com.example.email_service.service;

import com.example.email_service.dto.EmailQueueMessage;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import com.sendgrid.*;

import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailDispatchServiceTest {

    @Mock
    private SendGrid sendGrid;

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private EmailDispatchService service;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        System.out.println("Starting EmailDispatchServiceTest");
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
        System.out.println("Finished EmailDispatchServiceTest");
    }

    @Test
    void sendEmail_success_setsSentStatus() throws Exception {

        EmailNotification email = new EmailNotification();
        email.setDeliveryStatus(DeliveryStatus.QUEUED);

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

        assertEquals(DeliveryStatus.SENT, email.getDeliveryStatus());
        verify(emailRepository).save(email);

        System.out.println("sendEmail_success_setsSentStatus passed");
    }

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

        assertEquals(DeliveryStatus.FAILED, email.getDeliveryStatus());
        verify(emailRepository).save(email);

        System.out.println("sendEmail_failure_setsFailedStatus passed");
    }

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

        assertEquals(DeliveryStatus.FAILED, email.getDeliveryStatus());
        verify(emailRepository).save(email);

        System.out.println("sendEmail_exception_setsFailedStatus passed");
    }
}
