package com.servientrega.locker.controller;

import com.servientrega.locker.dto.DashboardDTO;
import com.servientrega.locker.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard statistics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard statistics", 
               description = "Returns today's activity and compartment statistics")
    public ResponseEntity<DashboardDTO.DashboardStats> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
