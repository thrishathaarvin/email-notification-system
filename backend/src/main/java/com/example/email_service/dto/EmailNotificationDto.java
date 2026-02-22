package com.example.email_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter

//This is used to expose email data from backend to frontend
public class EmailNotificationDto {
    private UUID id;
    private String recipientEmail;
    private String fromEmail;
    private String subject;
    private String body;
    private String deliveryStatus;
    private LocalDateTime createdAt;

}
