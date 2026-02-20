package com.example.email_service.controller;

import com.example.email_service.dto.ReportResponseDto;
import com.example.email_service.service.ReportService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    /**
     * Simple summary endpoint
     */
    @GetMapping("/summary")
    public ReportResponseDto getSummary() {
        return service.getSummary();
    }

    /**
     * Dashboard endpoint (for charts)
     */
    @GetMapping("/dashboard")
    public ReportResponseDto getDashboardData() {
        return service.getDashboardData();
    }
}
