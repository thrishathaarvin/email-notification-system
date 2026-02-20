package com.example.email_service.controller;

import com.example.email_service.dto.TemplateRequestDto;
import com.example.email_service.service.TemplateService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TemplateControllerTest {

    @Mock
    private TemplateService templateService;

    @InjectMocks
    private TemplateController templateController;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        System.out.println("Initializing mocks for TemplateControllerTest");
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        System.out.println("Closing mocks for TemplateControllerTest");
        closeable.close();
    }

    @Test
    void create_callsServiceCreateTemplate() {

        System.out.println("Starting test: create_callsServiceCreateTemplate");

        TemplateRequestDto dto =
                new TemplateRequestDto("welcome", "Hello", "Body");

        System.out.println("Stubbing templateService.createTemplate()");
        when(templateService.createTemplate(any()))
                .thenReturn(null);

        System.out.println("Calling templateController.create()");
        Object response = templateController.create(dto);

        System.out.println("Verifying service interaction");
        verify(templateService).createTemplate(dto);

        System.out.println("Test completed successfully");
    }

    @Test
    void getAllTemplates_returnsTemplatesFromService() {

        System.out.println("Starting test: getAllTemplates_returnsTemplatesFromService");

        List<TemplateRequestDto> mockList = List.of(
                new TemplateRequestDto("welcome", "Hi", "Body"),
                new TemplateRequestDto("reminder", "Pay", "Please pay")
        );

        System.out.println("Stubbing templateService.getAllTemplates()");
        when(templateService.getAllTemplates()).thenReturn(mockList);

        System.out.println("Calling templateController.getAllTemplates()");
        List<TemplateRequestDto> result =
                templateController.getAllTemplates();

        System.out.println("Asserting returned list size");
        assertEquals(2, result.size());

        System.out.println("Verifying service interaction");
        verify(templateService).getAllTemplates();

        System.out.println("Test completed successfully");
    }

    @Test
    void deleteTemplate_callsServiceDeleteTemplate() {

        System.out.println("Starting test: deleteTemplate_callsServiceDeleteTemplate");

        UUID templateId = UUID.randomUUID();

        System.out.println("Calling templateController.deleteTemplate()");
        templateController.deleteTemplate(templateId);

        System.out.println("Verifying service.deleteTemplate() was called");
        verify(templateService).deleteTemplate(templateId);

        System.out.println("Test completed successfully");
    }
}
