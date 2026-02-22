package com.example.email_service.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_STATUS_QUEUE = "email.status.queue";

    @Bean
    public Queue emailStatusQueue() {
        return new Queue(EMAIL_STATUS_QUEUE, true);
    }
}
