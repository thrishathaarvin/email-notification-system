package com.example.email_service.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter

//this is used to receive email send requests from frontend
public class EmailRequestDto {

    private String to;
    private String from;
    private String subject;
    private String body;
    private UUID templateId;

}
