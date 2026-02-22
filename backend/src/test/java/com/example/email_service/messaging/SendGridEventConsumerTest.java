package com.example.email_service.messaging;

import com.example.email_service.dto.SendGridEventDto;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.repository.DeliveryHistoryRepository;
import com.example.email_service.repository.EmailRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

class SendGridEventConsumerTest {

    @Mock
    private EmailRepository emailRepository;

    @Mock
    private DeliveryHistoryRepository historyRepository;

    @InjectMocks
    private SendGridEventConsumer consumer;

    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        System.out.println(" Initializing mocks for SendGridEventConsumerTest");
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void close() throws Exception {
        System.out.println(" Closing mocks after test execution");
        closeable.close();
    }

    //creates a mock mail, stubs the repo with it,
    //sends a fake sendgrid event and checks for update
    @Test
    void consume_updatesStatusAndHistory() {

        System.out.println(" Test started: consume_updatesStatusAndHistory");

        EmailNotification email = new EmailNotification();
        UUID emailId = UUID.randomUUID();
        email.setId(emailId);

        System.out.println(" Mock EmailNotification created with ID: " + emailId);

        when(emailRepository.findByProviderTrackingId(any()))
                .thenReturn(Optional.of(email));

        System.out.println(" Mocked emailRepository.findByProviderTrackingId()");

        SendGridEventDto event = new SendGridEventDto();
        event.setEvent("delivered");
        event.setSg_message_id("sg-123");

        System.out.println(" SendGridEventDto created");
        System.out.println("Event type: delivered");
        System.out.println("SendGrid Message ID: sg-123");

        System.out.println(" Calling consumer.consume(event)");
        consumer.consume(event);

        System.out.println(" consumer.consume(event) executed");

        verify(emailRepository).save(any());
        System.out.println("️ Verified: emailRepository.save() was called");

        verify(historyRepository).save(any());
        System.out.println(" Verified: historyRepository.save() was called");

        System.out.println(" Test completed successfully");
    }
}
