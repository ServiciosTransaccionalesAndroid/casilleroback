package com.servientrega.locker.controller;

import com.servientrega.locker.service.monitoring.MetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
@Tag(name = "Metrics", description = "Operational metrics endpoints")
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping("/locker/{lockerId}")
    @Operation(summary = "Get locker metrics", description = "Gets occupancy and availability metrics for a locker")
    public ResponseEntity<MetricsService.LockerMetrics> getLockerMetrics(@PathVariable Long lockerId) {
        MetricsService.LockerMetrics metrics = metricsService.getLockerMetrics(lockerId);
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/operational")
    @Operation(summary = "Get operational metrics", description = "Gets overall operational metrics")
    public ResponseEntity<MetricsService.OperationalMetrics> getOperationalMetrics() {
        MetricsService.OperationalMetrics metrics = metricsService.getOperationalMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/locker/{lockerId}/utilization")
    @Operation(summary = "Get compartment utilization", description = "Gets utilization by compartment size")
    public ResponseEntity<Map<String, Integer>> getCompartmentUtilization(@PathVariable Long lockerId) {
        Map<String, Integer> utilization = metricsService.getCompartmentUtilization(lockerId);
        return ResponseEntity.ok(utilization);
    }
}
