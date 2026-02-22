package com.example.email_service.service;

import com.example.email_service.dto.TemplateRequestDto;
import com.example.email_service.model.entity.EmailTemplate;
import com.example.email_service.repository.TemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

//handles CRUD for templates
@Service
public class TemplateService {

    //dependency injection
    private final TemplateRepository repository;
    public TemplateService(TemplateRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public TemplateRequestDto createTemplate(TemplateRequestDto dto) {
        EmailTemplate template = new EmailTemplate();
        template.setTemplateName(dto.getName());
        template.setSubjectLine(dto.getSubject());
        template.setContentBody(dto.getBody());

        EmailTemplate saved = repository.save(template);
        return new TemplateRequestDto(saved); // ✅ return DTO with ID
    }

    // READ
    public List<TemplateRequestDto> getAllTemplates() {
        return repository.findAll()
                .stream()
                .map(TemplateRequestDto::new)
                .collect(Collectors.toList());
    }

    // DELETE
    public void deleteTemplate(UUID id) {
        repository.deleteById(id);
    }
}