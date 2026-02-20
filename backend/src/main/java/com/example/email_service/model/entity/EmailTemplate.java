package com.example.email_service.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
public class EmailTemplate {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String templateName;

    @Column(nullable = false)
    private String subjectLine;

    @Lob
    private String contentBody;

    @Column(columnDefinition = "json")
    private String placeholders;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

//    @Setter
//    private String name;
//
//    @Setter
//    private String subject;
//
//    @Setter
//    private String body;
//
}
