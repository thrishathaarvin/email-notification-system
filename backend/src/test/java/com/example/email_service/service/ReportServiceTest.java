package com.example.email_service.service;

import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;

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
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    //unit test for summary, mocking the repo
    @Test
    void getSummary_returnsCorrectCounts() {
        when(emailRepository.countByStatus(DeliveryStatus.SENT)).thenReturn(5L);
        when(emailRepository.countByStatus(DeliveryStatus.FAILED)).thenReturn(2L);

        var dto = reportService.getSummary();

        assertEquals(5, dto.getSent());
        assertEquals(2, dto.getFailed());
        assertNotNull(dto.getDailyCounts());
        assertNotNull(dto.getStatusCounts());
    }

    //checks if dashboard returns correct values
    @Test
    void getDashboardData_returnsCorrectMetrics() {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeDaysAgo = now.minusDays(3);

        EmailNotification e1 = new EmailNotification();
        e1.setCreatedAt(now.minusDays(0));
        e1.setStatus(DeliveryStatus.SENT);

        EmailNotification e2 = new EmailNotification();
        e2.setCreatedAt(now.minusDays(1));
        e2.setStatus(DeliveryStatus.FAILED);

        EmailNotification e3 = new EmailNotification();
        e3.setCreatedAt(now.minusDays(1));
        e3.setStatus(DeliveryStatus.SENT);

        EmailNotification e4 = new EmailNotification();
        e4.setCreatedAt(now.minusDays(3));
        e4.setStatus(DeliveryStatus.SENT);

        List<EmailNotification> recentEmails = List.of(e1, e2, e3, e4);

        when(emailRepository.findAllByCreatedAtBetween(any(), any()))
                .thenReturn(recentEmails);

        for (DeliveryStatus status : DeliveryStatus.values()) {
            long count = recentEmails.stream().filter(e -> e.getStatus() == status).count();
            when(emailRepository.countByStatus(status)).thenReturn(count);
        }

        var dto = reportService.getDashboardData();

        //Status counts
        assertEquals(3L, dto.getSent());
        assertEquals(1L, dto.getFailed());

        assertTrue(dto.getStatusCounts().containsKey("SENT"));
        assertTrue(dto.getStatusCounts().containsKey("FAILED"));

        //Daily counts - TreeMap ensures ascending
        assertEquals(7, dto.getDailyCounts().size()); // 7 days filled
        dto.getDailyCounts().values().forEach(count -> assertNotNull(count));
    }

    //handles daily count chart
    @Test
    void getDashboardData_handlesNoEmailsGracefully() {
        when(emailRepository.findAllByCreatedAtBetween(any(), any()))
                .thenReturn(List.of());

        for (DeliveryStatus status : DeliveryStatus.values()) {
            when(emailRepository.countByStatus(status)).thenReturn(0L);
        }

        var dto = reportService.getDashboardData();

        assertEquals(0L, dto.getSent());
        assertEquals(0L, dto.getFailed());

        assertEquals(7, dto.getDailyCounts().size()); // 7 days zero
        dto.getDailyCounts().values().forEach(count -> assertEquals(0L, count));

        dto.getStatusCounts().values().forEach(count -> assertEquals(0L, count));
    }
}