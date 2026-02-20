package com.example.email_service.controller;

import com.example.email_service.dto.SendGridEventDto;
import com.example.email_service.messaging.StatusEventProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/webhooks/sendgrid")
public class WebhookController {

    private final StatusEventProducer statusProducer;

    public WebhookController(StatusEventProducer statusProducer) {
        this.statusProducer = statusProducer;
    }

    @PostMapping
    public ResponseEntity<Void> receiveEvents(@RequestBody List<SendGridEventDto> events) {

        for (SendGridEventDto event : events) {
            statusProducer.publishStatus(event); // sends to EMAIL_STATUS_QUEUE
        }

        return ResponseEntity.ok().build();
    }
}
