package com.example.email_service.service;

import com.example.email_service.dto.TemplateRequestDto;
import com.example.email_service.repository.TemplateRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.mockito.Mockito.*;

class TemplateServiceTest {

    @Mock
    private TemplateRepository repository;

    @InjectMocks
    private TemplateService service;

    private AutoCloseable closeable;

    @BeforeEach
    void init() {
        System.out.println("Initializing mocks for TemplateServiceTest");
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void close() throws Exception {
        System.out.println("Closing mocks after test execution");
        closeable.close();
    }

    @Test
    void createTemplate_savesTemplate() {

        System.out.println("Starting test: createTemplate_savesTemplate");

        TemplateRequestDto dto =
                new TemplateRequestDto("welcome", "Hi", "Body");

        System.out.println("Created TemplateRequestDto with name=welcome");

        System.out.println("Calling service.createTemplate()");
        service.createTemplate(dto);

        System.out.println("Verifying repository.save() was called");
        verify(repository).save(any());

        System.out.println("Test completed successfully");
    }
}
