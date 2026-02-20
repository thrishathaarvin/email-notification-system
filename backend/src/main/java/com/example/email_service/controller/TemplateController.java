package com.example.email_service.controller;

import com.example.email_service.dto.TemplateRequestDto;
import com.example.email_service.service.TemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private final TemplateService service;

    public TemplateController(TemplateService service) {
        this.service = service;
    }

    @PostMapping
    public Object create(@RequestBody TemplateRequestDto dto) {
        return service.createTemplate(dto);
    }

    @GetMapping
    public List<TemplateRequestDto> getAllTemplates() {
        return service.getAllTemplates(); // implement this in TemplateService
    }

    @DeleteMapping("/{id}")
    public void deleteTemplate(@PathVariable UUID id) {
        service.deleteTemplate(id);
    }


}
