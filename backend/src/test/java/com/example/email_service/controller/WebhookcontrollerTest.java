package com.example.email_service.controller;

import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.service.WebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class WebhookControllerTest {

    private WebhookService webhookService;
    private WebhookController webhookController;

    @BeforeEach
    void setUp() {
        webhookService = mock(WebhookService.class);
        webhookController = new WebhookController(webhookService);
    }

    @Test
    void testHandleSendGridEvent_singleEvent() {
        Map<String, Object> event = new HashMap<>();
        event.put("sg_message_id", "abcd1234.sendgrid");
        event.put("event", "delivered");
        event.put("email", "test@example.com");

        webhookController.handleSendGridEvent(new Map[]{event});

        // capture arguments passed to webhookService
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<DeliveryStatus> statusCaptor = ArgumentCaptor.forClass(DeliveryStatus.class);
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);

        verify(webhookService, times(1))
                .handleStatusUpdate(idCaptor.capture(), statusCaptor.capture(), emailCaptor.capture());

        assertEquals("abcd1234", idCaptor.getValue()); // cleanMessageId
        assertEquals(DeliveryStatus.SENT, statusCaptor.getValue());
        assertEquals("test@example.com", emailCaptor.getValue());
    }

    @Test
    void testHandleSendGridEvent_multipleEvents() {
        Map<String, Object> event1 = new HashMap<>();
        event1.put("sg_message_id", "msg1.sendgrid");
        event1.put("event", "open");
        event1.put("email", "a@example.com");

        Map<String, Object> event2 = new HashMap<>();
        event2.put("sg_message_id", "msg2.sendgrid");
        event2.put("event", "bounce");
        event2.put("email", "b@example.com");

        webhookController.handleSendGridEvent(new Map[]{event1, event2});

        // verify that handleStatusUpdate is called twice
        verify(webhookService, times(2)).handleStatusUpdate(anyString(), any(DeliveryStatus.class), anyString());

        // optional: capture arguments for each call
        ArgumentCaptor<String> idCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<DeliveryStatus> statusCaptor = ArgumentCaptor.forClass(DeliveryStatus.class);
        ArgumentCaptor<String> emailCaptor = ArgumentCaptor.forClass(String.class);

        verify(webhookService, times(2))
                .handleStatusUpdate(idCaptor.capture(), statusCaptor.capture(), emailCaptor.capture());

        // first event
        assertEquals("msg1", idCaptor.getAllValues().get(0));
        assertEquals(DeliveryStatus.OPENED, statusCaptor.getAllValues().get(0));
        assertEquals("a@example.com", emailCaptor.getAllValues().get(0));

        // second event
        assertEquals("msg2", idCaptor.getAllValues().get(1));
        assertEquals(DeliveryStatus.BOUNCED, statusCaptor.getAllValues().get(1));
        assertEquals("b@example.com", emailCaptor.getAllValues().get(1));
    }

    @Test
    void testHandleSendGridEvent_unknownEvent() {
        Map<String, Object> event = new HashMap<>();
        event.put("sg_message_id", "xyz123.sendgrid");
        event.put("event", "spamreport"); // unknown event
        event.put("email", "spam@example.com");

        webhookController.handleSendGridEvent(new Map[]{event});

        ArgumentCaptor<DeliveryStatus> statusCaptor = ArgumentCaptor.forClass(DeliveryStatus.class);
        verify(webhookService).handleStatusUpdate(anyString(), statusCaptor.capture(), anyString());

        assertEquals(DeliveryStatus.UNKNOWN, statusCaptor.getValue());
    }

    @Test
    void testHandleSendGridEvent_exceptionIsLogged() {
        Map<String, Object> event = new HashMap<>();
        event.put("sg_message_id", null); // will cause NPE inside controller
        event.put("event", "delivered");
        event.put("email", "test@example.com");

        // should not throw, exception is caught inside controller
        webhookController.handleSendGridEvent(new Map[]{event});

        // webhookService should not be called
        verify(webhookService, never()).handleStatusUpdate(anyString(), any(), anyString());
    }
}