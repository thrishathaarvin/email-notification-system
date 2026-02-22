package com.example.email_service.dto;

import lombok.Getter;

import java.util.Map;

@Getter

//This represents reporting data to return to frontend
public class ReportResponseDto {

    private final long sent;
    private final long failed;
    private final Map<String, Long> dailyCounts;
    private final Map<String, Long> statusCounts;

    // Constructor for simple summary
    public ReportResponseDto(long sent, long failed) {
        this.sent = sent;
        this.failed = failed;
        this.dailyCounts = Map.of();
        this.statusCounts = Map.of();
    }

    // Constructor for full dashboard
    public ReportResponseDto(long sent, long failed, Map<String, Long> dailyCounts, Map<String, Long> statusCounts) {
        this.sent = sent;
        this.failed = failed;
        this.dailyCounts = dailyCounts;
        this.statusCounts = statusCounts;
    }

}
