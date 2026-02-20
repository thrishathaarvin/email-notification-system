package com.example.email_service.service;

import com.example.email_service.dto.EmailQueueMessage;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class EmailDispatchService {


    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    private final SendGrid sendGrid;
    private final EmailRepository emailRepository;

    public EmailDispatchService(SendGrid sendGrid, EmailRepository emailRepository) {
        this.sendGrid = sendGrid;
        this.emailRepository = emailRepository;
    }

    public void sendEmail(EmailQueueMessage message) {

        EmailNotification email = emailRepository
                .findById(message.getEmailId())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // Use the 'from' field from the message
        Email fromEmail = new Email(message.getFrom());  // <--- dynamic sender
        Email toEmail = new Email(message.getTo());
        Content content = new Content("text/html", message.getBody());
        Mail mail = new Mail(fromEmail, message.getSubject(), toEmail, content);

        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sendGrid.api(request);

            if (response.getStatusCode() == 202) {
                email.setDeliveryStatus(DeliveryStatus.SENT);

                // Correct: just get the header as String
                String messageId = response.getHeaders().get("X-Message-Id");
                email.setProviderTrackingId(messageId);

            } else {
                email.setDeliveryStatus(DeliveryStatus.FAILED);
            }

        } catch (Exception e) {
            email.setDeliveryStatus(DeliveryStatus.FAILED);
        }

        emailRepository.save(email);
    }

    public String sendEmailDirectly(String from, String to, String subject, String body) throws IOException, IOException {
        Email fromEmail = new Email(from);
        Email toEmail = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(fromEmail, subject, toEmail, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        request.setMethod(Method.POST);
        request.setEndpoint("mail/send");
        request.setBody(mail.build());

        Response response = sg.api(request);

        if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
            // get the SendGrid message ID
            String messageId = response.getHeaders().get("X-Message-Id"); // this is usually a String
            return messageId;
        } else {
            throw new RuntimeException("SendGrid returned error: " + response.getBody());
        }
    }

}
