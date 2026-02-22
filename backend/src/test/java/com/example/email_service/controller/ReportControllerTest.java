package com.example.email_service.controller;

import com.example.email_service.dto.ReportResponseDto;
import com.example.email_service.service.ReportService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        System.out.println("Initializing mocks for ReportControllerTest");
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        System.out.println("Closing mocks for ReportControllerTest");
        closeable.close();
    }

    //for summary - here we mock the response (5 sent, 2 failed), then we stub the service method to return this mock
    @Test
    void getSummary_returnsSummaryFromService() {

        System.out.println("Starting test: getSummary_returnsSummaryFromService");

        ReportResponseDto mockResponse = new ReportResponseDto(5, 2);

        System.out.println("Stubbing reportService.getSummary()");
        when(reportService.getSummary()).thenReturn(mockResponse);

        System.out.println("Calling reportController.getSummary()");
        ReportResponseDto response = reportController.getSummary();

        System.out.println("Asserting response values");
        assertEquals(5, response.getSent());
        assertEquals(2, response.getFailed());

        System.out.println("Verifying service interaction");
        verify(reportService).getSummary();

        System.out.println("Test completed successfully");
    }

    //for dashboard - same as above
    @Test
    void getDashboardData_returnsDashboardDataFromService() {

        System.out.println("Starting test: getDashboardData_returnsDashboardDataFromService");

        ReportResponseDto mockResponse =
                new ReportResponseDto(10, 3);

        System.out.println("Stubbing reportService.getDashboardData()");
        when(reportService.getDashboardData()).thenReturn(mockResponse);

        System.out.println("Calling reportController.getDashboardData()");
        ReportResponseDto response = reportController.getDashboardData();

        System.out.println("Asserting dashboard response values");
        assertEquals(10, response.getSent());
        assertEquals(3, response.getFailed());

        System.out.println("Verifying service interaction");
        verify(reportService).getDashboardData();

        System.out.println("Test completed successfully");
    }
}
