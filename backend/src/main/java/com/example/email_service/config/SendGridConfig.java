package com.example.email_service.config;

import com.sendgrid.SendGrid;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SendGridConfig {

    @Bean
    public SendGrid sendGrid() {
        return new SendGrid(System.getenv("SENDGRID_API_KEY"));
    }
}
