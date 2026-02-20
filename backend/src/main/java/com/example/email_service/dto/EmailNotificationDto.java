package com.example.email_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
public class EmailNotificationDto {
    // ✅ Getters & Setters
    private UUID id;
    private String recipientEmail;
    private String fromEmail;
    private String subject;
    private String body;
    private String deliveryStatus;
    private LocalDateTime createdAt;

}
