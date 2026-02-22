package com.example.email_service.controller;

import com.example.email_service.dto.ReportResponseDto;
import com.example.email_service.service.ReportService;
import org.springframework.web.bind.annotation.*;

//This exposes API for read only reports and dashboard
@RestController
@RequestMapping("/api/reports")
public class ReportController {

    //Constructor injection
    private final ReportService service;
    public ReportController(ReportService service) {
        this.service = service;
    }

    //Returns sent and failed email counts
    @GetMapping("/summary")
    public ReportResponseDto getSummary() {
        return service.getSummary();
    }

    //Visualization purposes
    @GetMapping("/dashboard")
    public ReportResponseDto getDashboardData() {
        return service.getDashboardData();
    }
}
