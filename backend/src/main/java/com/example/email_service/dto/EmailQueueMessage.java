package com.example.email_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Setter
@Getter

//This defines the payload data for async email processing using queue
public class EmailQueueMessage implements Serializable {

    private UUID emailId;
    private String from;
    private String to;
    private String subject;
    private String body;
    private Long templateId;

}
