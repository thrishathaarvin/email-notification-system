package com.example.email_service.controller;

import com.example.email_service.dto.TemplateRequestDto;
import com.example.email_service.service.TemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

//This exposes API for managing email templates
@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    //Constructor Injection
    private final TemplateService service;
    public TemplateController(TemplateService service) {
        this.service = service;
    }

    //Creates new template
    @PostMapping
    public Object create(@RequestBody TemplateRequestDto dto) {
        return service.createTemplate(dto);
    }

    //Fetch and display all available templates
    @GetMapping
    public List<TemplateRequestDto> getAllTemplates() {
        return service.getAllTemplates();
    }

    //Deletes template
    @DeleteMapping("/{id}")
    public void deleteTemplate(@PathVariable UUID id) {
        service.deleteTemplate(id);
    }

}
