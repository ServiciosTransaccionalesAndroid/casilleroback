package com.servientrega.locker.controller;

import com.servientrega.locker.entity.Alert;
import com.servientrega.locker.service.monitoring.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Alert management endpoints")
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/locker/{lockerId}")
    @Operation(summary = "Get active alerts", description = "Gets all active alerts for a locker")
    public ResponseEntity<List<AlertResponse>> getActiveAlerts(@PathVariable Long lockerId) {
        List<Alert> alerts = alertService.getActiveAlerts(lockerId);
        
        List<AlertResponse> response = alerts.stream()
            .map(a -> new AlertResponse(
                a.getId(),
                a.getLocker().getId(),
                a.getCompartment() != null ? a.getCompartment().getId() : null,
                a.getAlertType().name(),
                a.getSeverity().name(),
                a.getMessage(),
                a.getCreatedAt()
            ))
            .toList();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{alertId}/resolve")
    @Operation(summary = "Resolve alert", description = "Marks an alert as resolved")
    public ResponseEntity<String> resolveAlert(@PathVariable Long alertId) {
        alertService.resolveAlert(alertId);
        return ResponseEntity.ok("Alert resolved successfully");
    }

    public record AlertResponse(
        Long id,
        Long lockerId,
        Long compartmentId,
        String alertType,
        String severity,
        String message,
        java.time.LocalDateTime createdAt
    ) {}
}
