package com.example.email_service.messaging;

import com.example.email_service.config.RabbitMQConfig;
import com.example.email_service.dto.SendGridEventDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

//This publishes the sendgrid events inside the queue
@Component
public class StatusEventProducer {

    //Constructor injection
    private final RabbitTemplate rabbitTemplate;
    public StatusEventProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    //event is converted to JSON, then sent to queue, from which consumers use
    public void publishStatus(SendGridEventDto event) {
        rabbitTemplate.convertAndSend(RabbitMQConfig.EMAIL_STATUS_QUEUE, event);
    }
}
