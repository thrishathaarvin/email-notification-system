package com.example.email_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

//This represents events sent by sendgrid
public class SendGridEventDto {
    private String event;
    private String sg_message_id;
    private String email;
    private Long timestamp;
}
