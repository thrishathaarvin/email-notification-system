package com.example.email_service.dto;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Getter
@Setter
public class SendGridEventDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String event;
    private String sg_message_id;
    private String email;
    private Long timestamp;
}