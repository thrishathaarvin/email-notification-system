package com.example.email_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendGridEventDto {


    private String event;        // sent, delivered, open, bounce

    private String sg_message_id;
    private String email;
    private Long timestamp;
}
