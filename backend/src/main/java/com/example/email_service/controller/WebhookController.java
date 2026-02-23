package com.example.email_service.controller;

import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.service.WebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/webhooks/sendgrid")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @PostMapping
    public void handleSendGridEvent(@RequestBody Map<String, Object>[] events) {
        for (Map<String, Object> event : events) {
            try {
                String msgId = (String) event.get("sg_message_id");
                String statusStr = (String) event.get("event");
                String email = (String) event.get("email");

                // Skip events with missing SendGrid ID
                if (msgId == null || msgId.isBlank()) {
                    log.warn("Webhook event received with missing SendGrid ID: {}", event);
                    continue;
                }

                // Clean message ID (remove dot suffix if present)
                String cleanMessageId = msgId.contains(".")
                        ? msgId.substring(0, msgId.indexOf('.'))
                        : msgId;

                // Map SendGrid event to DeliveryStatus enum
                DeliveryStatus status;
                if (statusStr == null) {
                    status = DeliveryStatus.UNKNOWN;
                } else {
                    switch (statusStr.toLowerCase()) {
                        case "delivered": status = DeliveryStatus.SENT; break;
                        case "open": status = DeliveryStatus.OPENED; break;
                        case "bounce": status = DeliveryStatus.BOUNCED; break;
                        case "dropped": status = DeliveryStatus.FAILED; break;
                        default: status = DeliveryStatus.UNKNOWN; break;
                    }
                }

                webhookService.handleStatusUpdate(cleanMessageId, status, email);

            } catch (Exception e) {
                log.warn("Failed to process webhook event: {}", event, e);
            }
        }
    }
}