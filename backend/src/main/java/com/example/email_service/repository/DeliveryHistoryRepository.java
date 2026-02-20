package com.example.email_service.repository;

import com.example.email_service.model.entity.EmailDeliveryHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryHistoryRepository extends JpaRepository<EmailDeliveryHistory, UUID> {
}
