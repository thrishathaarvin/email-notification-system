package com.example.email_service.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class ReportResponseDto {

    private final long sent;
    private final long failed;
    private final double successRatio;

    // Dashboard charts
    private final Map<String, Long> dailyCounts;    // "2026-02-19" -> 15 emails (line chart)
     // templateId -> count
    private final Map<String, Long> statusCounts;   // "SENT" -> 5, "FAILED" -> 2 (bar chart)

    // Constructor for simple summary (backward compatible)
    public ReportResponseDto(long sent, long failed) {
        this.sent = sent;
        this.failed = failed;
        this.successRatio = sent + failed == 0 ? 0 : (double) sent / (sent + failed);
        this.dailyCounts = Map.of();

        this.statusCounts = Map.of();
    }

    // Constructor for full dashboard
    public ReportResponseDto(long sent, long failed, Map<String, Long> dailyCounts,
                              Map<String, Long> statusCounts) {
        this.sent = sent;
        this.failed = failed;
        this.successRatio = sent + failed == 0 ? 0 : (double) sent / (sent + failed);
        this.dailyCounts = dailyCounts;

        this.statusCounts = statusCounts;
    }

    // Empty constructor for deserialization (optional)
    public ReportResponseDto() {
        this.sent = 0;
        this.failed = 0;
        this.successRatio = 0;
        this.dailyCounts = Map.of();

        this.statusCounts = Map.of();
    }
}
