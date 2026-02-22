package com.example.email_service.controller;

import com.example.email_service.dto.SendGridEventDto;
import com.example.email_service.messaging.StatusEventProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//this receives sendgrid events and forward it to queue, which is now not done due to being locally hosted
@RestController
@RequestMapping("/api/webhooks/sendgrid")
public class WebhookController {

    //Constructor injection
    private final StatusEventProducer statusProducer;
    public WebhookController(StatusEventProducer statusProducer) {
        this.statusProducer = statusProducer;
    }

    //fetches status and pushes to status queue
    @PostMapping
    public ResponseEntity<Void> receiveEvents(@RequestBody List<SendGridEventDto> events) {

        for (SendGridEventDto event : events) {
            statusProducer.publishStatus(event);
        }

        return ResponseEntity.ok().build();
    }
}
