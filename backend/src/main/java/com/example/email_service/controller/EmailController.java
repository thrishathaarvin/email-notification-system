package com.example.email_service.controller;

import com.example.email_service.dto.EmailRequestDto;
import com.example.email_service.dto.EmailNotificationDto;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import com.example.email_service.service.EmailDispatchService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

//This is where we expose API for:
//1. Sending email
//2. Fetch emails based on status
//3. View a mail
//4. Delete a mail

@RestController
@RequestMapping("/api/emails")
public class EmailController {

    //Here we perform constructor injection
    private final EmailRepository emailRepository;
    private final EmailDispatchService dispatchService;

    public EmailController(EmailRepository emailRepository,
                           EmailDispatchService dispatchService) {
        this.emailRepository = emailRepository;
        this.dispatchService = dispatchService;
    }

    // here, this is mainly used to fetch all emails, filters are optional (mail status)
    //Response entity represents entire http response not just body (headers, body, status code)

    @GetMapping
    public ResponseEntity<List<EmailNotificationDto>> getAllEmails(
            @RequestParam(required = false) String status) {
        DeliveryStatus deliveryStatus = null;
        if (status != null && !status.isBlank()) {
            deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());
        }

        List<EmailNotification> emails = emailRepository.filterEmails(
                deliveryStatus,
                null,
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
            dto.setDeliveryStatus(email.getStatus().name());
            dto.setCreatedAt(email.getCreatedAt());
            return dto;
        }).toList();

        return ResponseEntity.ok(dtos);
    }

    //this handles sending email through send grid, persists the email first, then sends
    @PostMapping("/send")
    public String sendEmail(@RequestBody EmailRequestDto request) {
        EmailNotification email = new EmailNotification();
        email.setRecipientEmail(request.getTo());
        email.setFinalSubject(request.getSubject());
        email.setFinalBody(request.getBody());
        email.setSenderEmail(request.getFrom());
        email.setStatus(DeliveryStatus.CREATED);
        email.setCreatedAt(LocalDateTime.now());

        email = emailRepository.save(email);

        try {
            String providerId = dispatchService.sendEmailDirectly(
                    request.getFrom(), request.getTo(),
                    request.getSubject(), request.getBody()
            );

            email.setStatus(DeliveryStatus.SENT);
            email.setProviderTrackingId(providerId);
            emailRepository.save(email);

            return "Email sent successfully";
        } catch (Exception e) {
            email.setStatus(DeliveryStatus.FAILED);
            emailRepository.save(email);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }

    //This is for fetching single mail, mainly for email details page
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
        dto.setDeliveryStatus(email.getStatus().name());
        dto.setCreatedAt(email.getCreatedAt());

        return ResponseEntity.ok(dto);
    }

    //This is to delete mail if it exists in repo
    @DeleteMapping("/{id}")
    public String deleteEmail(@PathVariable UUID id) {
        if (!emailRepository.existsById(id)) {
            return "Email with id " + id + " not found";
        }

        emailRepository.deleteById(id);
        return "Email with id " + id + " deleted successfully";
    }
}
