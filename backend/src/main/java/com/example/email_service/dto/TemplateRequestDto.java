package com.example.email_service.dto;

import com.example.email_service.model.entity.EmailTemplate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequestDto {

    private UUID id;
    private String name;
    private String subject;
    private String body;

    public TemplateRequestDto(EmailTemplate template) {
        this.id=template.getId();
        this.name = template.getTemplateName();
        this.subject = template.getSubjectLine();
        this.body = template.getContentBody();
    }

    public TemplateRequestDto(String templateName, String subjectLine, String contentBody) {
        this.name=templateName;
        this.subject=subjectLine;
        this.body=contentBody;

    }
}
