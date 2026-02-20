package com.example.email_service.messaging;

import com.example.email_service.config.RabbitMQConfig;
import com.example.email_service.dto.SendGridEventDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class StatusEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public StatusEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishStatus(SendGridEventDto event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_STATUS_QUEUE, event);
    }
}
