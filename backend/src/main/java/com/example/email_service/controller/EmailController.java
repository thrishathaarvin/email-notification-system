package com.example.email_service.controller;

import com.example.email_service.dto.EmailRequestDto;
import com.example.email_service.dto.EmailNotificationDto;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import com.example.email_service.service.EmailDispatchService;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    private final EmailRepository emailRepository;
    private final EmailDispatchService dispatchService;

    public EmailController(EmailRepository emailRepository,
                           EmailDispatchService dispatchService) {
        this.emailRepository = emailRepository;
        this.dispatchService = dispatchService;
    }

    // ✅ GET emails with optional status & recipient filters
    @GetMapping
    public ResponseEntity<List<EmailNotificationDto>> getAllEmails(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String recipient
    ) {
        DeliveryStatus deliveryStatus = null;
        if (status != null && !status.isBlank()) {
            deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());
        }

        List<EmailNotification> emails = emailRepository.filterEmails(
                deliveryStatus,
                recipient,
                null,
                null
        );

        List<EmailNotificationDto> dtos = emails.stream().map(email -> {
            EmailNotificationDto dto = new EmailNotificationDto();
            dto.setId(email.getId());
            dto.setRecipientEmail(email.getRecipientEmail());
            dto.setFromEmail(email.getSenderEmail());
            dto.setSubject(email.getFinalSubject());
            dto.setBody(email.getFinalBody());
            dto.setDeliveryStatus(email.getDeliveryStatus().name());
            dto.setCreatedAt(email.getCreatedAt());
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequestDto request) {
        EmailNotification email = new EmailNotification();
        email.setRecipientEmail(request.getTo());
        email.setFinalSubject(request.getSubject());
        email.setFinalBody(request.getBody());
        email.setSenderEmail(request.getFrom());
        email.setDeliveryStatus(DeliveryStatus.CREATED);
        email.setCreatedAt(LocalDateTime.now());

        email = emailRepository.save(email);

        try {
            String providerId = dispatchService.sendEmailDirectly(
                    request.getFrom(), request.getTo(),
                    request.getSubject(), request.getBody()
            );

            email.setDeliveryStatus(DeliveryStatus.SENT);
            email.setProviderTrackingId(providerId);
            emailRepository.save(email);

            return "Email sent successfully";
        } catch (Exception e) {
            email.setDeliveryStatus(DeliveryStatus.FAILED);
            emailRepository.save(email);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailNotificationDto> getEmailById(@PathVariable UUID id) {
        EmailNotification email = emailRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Email not found: " + id));

        EmailNotificationDto dto = new EmailNotificationDto();
        dto.setId(email.getId());
        dto.setRecipientEmail(email.getRecipientEmail());
        dto.setFromEmail(email.getSenderEmail());
        dto.setSubject(email.getFinalSubject());
        dto.setBody(email.getFinalBody());
        dto.setDeliveryStatus(email.getDeliveryStatus().name());
        dto.setCreatedAt(email.getCreatedAt());

        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public String deleteEmail(@PathVariable UUID id) {
        if (!emailRepository.existsById(id)) {
            return "Email with id " + id + " not found";
        }

        emailRepository.deleteById(id);
        return "Email with id " + id + " deleted successfully";
    }
}
