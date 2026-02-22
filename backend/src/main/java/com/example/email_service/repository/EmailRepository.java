package com.example.email_service.repository;

import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//this has custom queries for filter using status and dashboard graph purposes
public interface EmailRepository extends JpaRepository<EmailNotification, UUID> {

    long countByStatus(DeliveryStatus deliveryStatus);

    Optional<EmailNotification> findByProviderTrackingId(String providerTrackingId);

    @Query("SELECT e FROM EmailNotification e " +
            "WHERE (:status IS NULL OR e.status = :status) " +
            "AND (:recipient IS NULL OR e.recipientEmail LIKE %:recipient%) " +
            "AND (:start IS NULL OR e.createdAt >= :start) " +
            "AND (:end IS NULL OR e.createdAt <= :end)")
    List<EmailNotification> filterEmails(
            @Param("status") DeliveryStatus status,
            @Param("recipient") String recipient,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<EmailNotification> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
