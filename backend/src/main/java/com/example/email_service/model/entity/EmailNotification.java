package com.example.email_service.model.entity;

import com.example.email_service.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

//This is for storing the email data and status
@Entity
@Table(name = "email_notification")
@Getter
@Setter
public class EmailNotification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String senderEmail;

    @Column(nullable = false)
    private String finalSubject;

    @Lob
    @Column(nullable = false)
    private String finalBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Getter
    private DeliveryStatus status;

    private String providerTrackingId;

    @Column(nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Getter
    private UUID templateId;
}
