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

//Email template data to be exchanged b/w frontend and backend
public class TemplateRequestDto {

    private UUID id;
    private String name;
    private String subject;
    private String body;

    //To return existing templates to frontend
    public TemplateRequestDto(EmailTemplate template) {
        this.id=template.getId();
        this.name = template.getTemplateName();
        this.subject = template.getSubjectLine();
        this.body = template.getContentBody();
    }

    //To create new template
    public TemplateRequestDto(String templateName, String subjectLine, String contentBody) {
        this.name=templateName;
        this.subject=subjectLine;
        this.body=contentBody;

    }
}
