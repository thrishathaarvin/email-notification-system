package com.example.email_service.service;

import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportServiceTest {

    @Mock
    private EmailRepository emailRepository;

    @InjectMocks
    private ReportService reportService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        System.out.println("Setting up mocks for ReportServiceTest");
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        System.out.println("Tearing down mocks after test execution");
        closeable.close();
    }

    @Test
    void getSummary_returnsCorrectCounts() {

        System.out.println("Starting test: getSummary_returnsCorrectCounts");

        when(emailRepository.countByStatus(DeliveryStatus.SENT))
                .thenReturn(5L);
        System.out.println("Mocked countByStatus(SENT) to return 5");

        when(emailRepository.countByStatus(DeliveryStatus.FAILED))
                .thenReturn(2L);
        System.out.println("Mocked countByStatus(FAILED) to return 2");

        System.out.println("Calling reportService.getSummary()");
        var dto = reportService.getSummary();

        System.out.println("Asserting sent count");
        assertEquals(5, dto.getSent());

        System.out.println("Asserting failed count");
        assertEquals(2, dto.getFailed());


        System.out.println("Test completed successfully");
    }
}
