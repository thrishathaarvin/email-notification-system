package com.example.email_service.service;

import com.example.email_service.dto.TemplateRequestDto;
import com.example.email_service.model.entity.EmailTemplate;
import com.example.email_service.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TemplateService {

    private final TemplateRepository repository;

    public TemplateService(TemplateRepository repository) {
        this.repository = repository;
    }

    public EmailTemplate createTemplate(TemplateRequestDto dto) {
        EmailTemplate template = new EmailTemplate();
        template.setTemplateName(dto.getName());
        template.setSubjectLine(dto.getSubject());
        template.setContentBody(dto.getBody());
        return repository.save(template);
    }

    public List<TemplateRequestDto> getAllTemplates() {
        return repository.findAll()
                .stream()
                .map(template -> new TemplateRequestDto(
                        template.getTemplateName(),    // name
                        template.getSubjectLine(),     // subject
                        template.getContentBody()      // body
                ))
                .collect(Collectors.toList());
    }

    public void deleteTemplate(UUID id) {
        repository.deleteById(id);
    }


}
