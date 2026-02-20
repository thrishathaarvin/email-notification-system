package com.example.email_service.model.entity;

import com.example.email_service.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_delivery_history")
@Getter
@Setter
public class EmailDeliveryHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID emailId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(nullable = false)
    private LocalDateTime recordedAt;

    @Lob
    private String providerMetadata;

}
