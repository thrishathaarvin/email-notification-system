package com.example.email_service.service;

import com.example.email_service.dto.ReportResponseDto;
import com.example.email_service.model.entity.EmailNotification;
import com.example.email_service.model.enums.DeliveryStatus;
import com.example.email_service.repository.EmailRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final EmailRepository emailRepository;

    public ReportService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    /**
     * Summary: total sent, failed emails
     */
    public ReportResponseDto getSummary() {
        long sent = emailRepository.countByStatus(DeliveryStatus.SENT);
        long failed = emailRepository.countByStatus(DeliveryStatus.FAILED);

        return new ReportResponseDto(sent, failed);
    }

    /**
     * Dashboard: daily counts (line chart) + status counts (bar chart) + template usage
     */
    public ReportResponseDto getDashboardData() {
        // 1️⃣ Bar chart: count by status
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (DeliveryStatus status : DeliveryStatus.values()) {
            long count = emailRepository.countByStatus(status);
            statusCounts.put(status.name(), count);
        }

        // 2️⃣ Line chart: last 7 days email activity
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(6); // last 7 days including today

        List<EmailNotification> recentEmails = emailRepository.findAllByCreatedAtBetween(weekAgo, now);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        Map<String, Long> dailyCounts = recentEmails.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCreatedAt().format(formatter),
                        TreeMap::new,
                        Collectors.counting()
                ));

        // fill missing dates with 0
        for (int i = 0; i <= 6; i++) {
            String day = weekAgo.plusDays(i).format(formatter);
            dailyCounts.putIfAbsent(day, 0L);
        }

        // 3️⃣ Template usage
//        Map<String, Long> templateUsage = recentEmails.stream()
//                .filter(e -> e.getTemplateId() != null)
//                .collect(Collectors.groupingBy(
//                        e -> e.getTemplateId().toString(),
//                        Collectors.counting()
//                ));

        // total sent/failed for summary
        long sent = statusCounts.getOrDefault("SENT", 0L);
        long failed = statusCounts.getOrDefault("FAILED", 0L);

        // Build final DTO
        return new ReportResponseDto(sent, failed, dailyCounts, statusCounts);
    }
}
