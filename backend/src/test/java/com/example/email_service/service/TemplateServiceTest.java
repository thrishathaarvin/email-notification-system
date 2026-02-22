package com.example.email_service.service;

import com.example.email_service.dto.TemplateRequestDto;
import com.example.email_service.model.entity.EmailTemplate;
import com.example.email_service.repository.TemplateRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TemplateServiceTest {

    @Mock
    private TemplateRepository repository;

    @InjectMocks
    private TemplateService service;

    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        System.out.println("=== Initializing mocks ===");
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void close() throws Exception {
        System.out.println("=== Closing mocks ===\n");
        closeable.close();
    }

    //create template
    @Test
    void createTemplate_savesTemplate() {
        System.out.println("=== Test: createTemplate_savesTemplate ===");

        TemplateRequestDto dto = new TemplateRequestDto("welcome", "Hi", "Body");
        System.out.println("Created TemplateRequestDto: " + dto.getName());

        EmailTemplate savedTemplate = new EmailTemplate();
        savedTemplate.setId(UUID.randomUUID());
        savedTemplate.setTemplateName(dto.getName());
        savedTemplate.setSubjectLine(dto.getSubject());
        savedTemplate.setContentBody(dto.getBody());

        when(repository.save(any(EmailTemplate.class))).thenReturn(savedTemplate);

        System.out.println("Calling service.createTemplate()");
        TemplateRequestDto result = service.createTemplate(dto);

        System.out.println("Verifying repository.save() called");
        verify(repository).save(any(EmailTemplate.class));

        System.out.println("Result DTO ID: " + result.getId());
        assertNotNull(result.getId());
        System.out.println("=== Test createTemplate_savesTemplate completed ===\n");
    }

    // get all templates
    @Test
    void getAllTemplates_returnsListOfDTOs() {
        System.out.println("=== Test: getAllTemplates_returnsListOfDTOs ===");

        EmailTemplate t1 = new EmailTemplate();
        t1.setId(UUID.randomUUID());
        t1.setTemplateName("welcome");
        t1.setSubjectLine("Hi");
        t1.setContentBody("Body");

        EmailTemplate t2 = new EmailTemplate();
        t2.setId(UUID.randomUUID());
        t2.setTemplateName("reminder");
        t2.setSubjectLine("Hello");
        t2.setContentBody("Body2");

        when(repository.findAll()).thenReturn(Arrays.asList(t1, t2));

        System.out.println("Calling service.getAllTemplates()");
        List<TemplateRequestDto> templates = service.getAllTemplates();

        System.out.println("Returned templates count: " + templates.size());
        templates.forEach(t -> System.out.println("Template: " + t.getName()));

        assertEquals(2, templates.size());
        verify(repository).findAll();

        System.out.println("=== Test getAllTemplates_returnsListOfDTOs completed ===\n");
    }

    // delete templates
    @Test
    void deleteTemplate_callsRepositoryDelete() {
        System.out.println("=== Test: deleteTemplate_callsRepositoryDelete ===");

        UUID id = UUID.randomUUID();
        System.out.println("Deleting template ID: " + id);

        service.deleteTemplate(id);

        System.out.println("Verifying repository.deleteById() called");
        verify(repository, times(1)).deleteById(id);

        System.out.println("=== Test deleteTemplate_callsRepositoryDelete completed ===\n");
    }
}