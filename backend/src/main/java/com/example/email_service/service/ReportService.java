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

//This generates summary metrics and chart metrics
@Service
public class ReportService {

    //dependency injection
    private final EmailRepository emailRepository;
    public ReportService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    //for displaying total sent and failed mails
    public ReportResponseDto getSummary() {
        long sent = emailRepository.countByStatus(DeliveryStatus.SENT);
        long failed = emailRepository.countByStatus(DeliveryStatus.FAILED);

        return new ReportResponseDto(sent, failed);
    }


    //for displaying dashboard
    public ReportResponseDto getDashboardData() {
        // 1. Bar chart: count by status
        Map<String, Long> statusCounts = new LinkedHashMap<>();
        for (DeliveryStatus status : DeliveryStatus.values()) {
            long count = emailRepository.countByStatus(status);
            statusCounts.put(status.name(), count);
        }

        // 2. Line chart: last 7 days email activity
        //used treemap so it automatically sorts dates in ascending
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minusDays(6);

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


        // final DTO building
        long sent = statusCounts.getOrDefault("SENT", 0L);
        long failed = statusCounts.getOrDefault("FAILED", 0L);
        return new ReportResponseDto(sent, failed, dailyCounts, statusCounts);
    }
}
