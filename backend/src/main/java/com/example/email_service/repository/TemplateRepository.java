package com.example.email_service.repository;
import com.example.email_service.model.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TemplateRepository extends JpaRepository<EmailTemplate, UUID> {
}
