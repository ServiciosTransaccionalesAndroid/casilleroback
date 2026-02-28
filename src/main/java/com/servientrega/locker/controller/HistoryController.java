package com.servientrega.locker.controller;

import com.servientrega.locker.dto.OperationHistoryDTO;
import com.servientrega.locker.enums.OperationType;
import com.servientrega.locker.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
@Tag(name = "History", description = "Operation history and audit trail endpoints")
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping("/package/{trackingNumber}")
    @Operation(
        summary = "Get package history",
        description = "Returns complete operation history for a specific package"
    )
    public ResponseEntity<List<OperationHistoryDTO>> getPackageHistory(
            @Parameter(description = "Package tracking number", example = "SRV123456789")
            @PathVariable String trackingNumber) {
        
        List<OperationHistoryDTO> history = historyService.getPackageHistory(trackingNumber);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/compartment/{compartmentId}")
    @Operation(
        summary = "Get compartment history",
        description = "Returns operation history for a specific compartment"
    )
    public ResponseEntity<List<OperationHistoryDTO>> getCompartmentHistory(
            @Parameter(description = "Compartment ID", example = "5")
            @PathVariable Long compartmentId) {
        
        List<OperationHistoryDTO> history = historyService.getCompartmentHistory(compartmentId);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/courier/{courierId}")
    @Operation(
        summary = "Get courier history",
        description = "Returns operation history for a specific courier within a date range"
    )
    public ResponseEntity<List<OperationHistoryDTO>> getCourierHistory(
            @Parameter(description = "Courier ID", example = "1")
            @PathVariable Long courierId,
            
            @Parameter(description = "Start date", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<OperationHistoryDTO> history = historyService.getCourierHistory(courierId, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/locker/{lockerId}")
    @Operation(
        summary = "Get locker history",
        description = "Returns operation history for a specific locker within a date range"
    )
    public ResponseEntity<List<OperationHistoryDTO>> getLockerHistory(
            @Parameter(description = "Locker ID", example = "1")
            @PathVariable Long lockerId,
            
            @Parameter(description = "Start date", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<OperationHistoryDTO> history = historyService.getLockerHistory(lockerId, startDate, endDate);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/operations")
    @Operation(
        summary = "Get operations by date range",
        description = "Returns all operations within a date range, optionally filtered by operation type"
    )
    public ResponseEntity<List<OperationHistoryDTO>> getOperations(
            @Parameter(description = "Start date", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Operation type filter (optional)", example = "DEPOSIT")
            @RequestParam(required = false) OperationType type) {
        
        List<OperationHistoryDTO> history = historyService.getOperationsByDateRange(startDate, endDate, type);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/operations/paged")
    @Operation(
        summary = "Get operations by date range (paginated)",
        description = "Returns paginated operations within a date range, optionally filtered by operation type"
    )
    public ResponseEntity<com.servientrega.locker.dto.PagedResponse<OperationHistoryDTO>> getOperationsPaged(
            @Parameter(description = "Start date", example = "2024-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Operation type filter (optional)", example = "DEPOSIT")
            @RequestParam(required = false) OperationType type,
            
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") int size) {
        
        var pagedHistory = historyService.getOperationsByDateRangePaged(startDate, endDate, type, page, size);
        return ResponseEntity.ok(pagedHistory);
    }
}
