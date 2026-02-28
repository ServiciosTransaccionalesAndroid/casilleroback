package com.servientrega.locker.controller;

import com.servientrega.locker.dto.ReportResponse;
import com.servientrega.locker.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Operational reports and statistics endpoints")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/deposits")
    @Operation(
        summary = "Get deposits report",
        description = "Returns deposit statistics for a given period, optionally filtered by locker"
    )
    public ResponseEntity<ReportResponse> getDepositsReport(
            @Parameter(description = "Start date", example = "2024-01-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Locker ID (optional)", example = "1")
            @RequestParam(required = false) Long lockerId) {
        
        ReportResponse report = reportService.getDepositsByPeriod(startDate, endDate, lockerId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/retrievals")
    @Operation(
        summary = "Get retrievals report",
        description = "Returns retrieval statistics for a given period, optionally filtered by locker"
    )
    public ResponseEntity<ReportResponse> getRetrievalsReport(
            @Parameter(description = "Start date", example = "2024-01-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Locker ID (optional)", example = "1")
            @RequestParam(required = false) Long lockerId) {
        
        ReportResponse report = reportService.getRetrievalsByPeriod(startDate, endDate, lockerId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/occupancy")
    @Operation(
        summary = "Get occupancy report",
        description = "Returns current occupancy statistics for a specific locker"
    )
    public ResponseEntity<ReportResponse> getOccupancyReport(
            @Parameter(description = "Locker ID", example = "1", required = true)
            @RequestParam Long lockerId) {
        
        ReportResponse report = reportService.getOccupancyRate(lockerId);
        return ResponseEntity.ok(report);
    }

    @GetMapping("/expired-codes")
    @Operation(
        summary = "Get expired codes report",
        description = "Returns list of retrieval codes that expired without being used in a given period"
    )
    public ResponseEntity<ReportResponse> getExpiredCodesReport(
            @Parameter(description = "Start date", example = "2024-01-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ReportResponse report = reportService.getExpiredCodes(startDate, endDate);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/courier-performance")
    @Operation(
        summary = "Get courier performance report",
        description = "Returns performance statistics for a courier or all couriers in a period"
    )
    public ResponseEntity<ReportResponse> getCourierPerformanceReport(
            @Parameter(description = "Courier ID (optional)", example = "1")
            @RequestParam(required = false) Long courierId,
            
            @Parameter(description = "Start date", example = "2024-01-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ReportResponse report = reportService.getCourierPerformance(courierId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/courier-deposits")
    @Operation(
        summary = "Get deposits by courier",
        description = "Returns total deposits grouped by courier for a period"
    )
    public ResponseEntity<ReportResponse> getCourierDepositsReport(
            @Parameter(description = "Start date", example = "2024-01-01", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        ReportResponse report = reportService.getDepositsByCourier(startDate, endDate);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/compartment-usage")
    @Operation(
        summary = "Get compartment usage report",
        description = "Returns usage statistics for all compartments in a locker"
    )
    public ResponseEntity<ReportResponse> getCompartmentUsageReport(
            @Parameter(description = "Locker ID", example = "1", required = true)
            @RequestParam Long lockerId) {
        
        ReportResponse report = reportService.getCompartmentUsage(lockerId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/active-packages")
    @Operation(
        summary = "Get active packages report",
        description = "Returns list of packages currently stored in a locker"
    )
    public ResponseEntity<ReportResponse> getActivePackagesReport(
            @Parameter(description = "Locker ID", example = "1", required = true)
            @RequestParam Long lockerId) {
        
        ReportResponse report = reportService.getActivePackages(lockerId);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/daily-summary")
    @Operation(
        summary = "Get daily summary report",
        description = "Returns consolidated summary of all operations for a specific date"
    )
    public ResponseEntity<ReportResponse> getDailySummaryReport(
            @Parameter(description = "Date", example = "2024-02-18", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        ReportResponse report = reportService.getDailySummary(date);
        return ResponseEntity.ok(report);
    }
    
    @GetMapping("/export/csv")
    @Operation(
        summary = "Export report to CSV",
        description = "Exports any report to CSV format"
    )
    public ResponseEntity<byte[]> exportToCsv(
            @Parameter(description = "Report type", required = true)
            @RequestParam String reportType,
            
            @Parameter(description = "Start date", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Locker ID (optional)")
            @RequestParam(required = false) Long lockerId) {
        
        // Generate report based on type
        ReportResponse report = generateReportByType(reportType, startDate, endDate, lockerId);
        
        // Export to CSV
        byte[] csvData = reportService.exportToCsv(report);
        
        return ResponseEntity.ok()
            .header("Content-Type", "text/csv")
            .header("Content-Disposition", "attachment; filename=\"report_" + reportType + ".csv\"")
            .body(csvData);
    }
    
    @GetMapping("/export/pdf")
    @Operation(
        summary = "Export report to PDF",
        description = "Exports any report to PDF format"
    )
    public ResponseEntity<byte[]> exportToPdf(
            @Parameter(description = "Report type", required = true)
            @RequestParam String reportType,
            
            @Parameter(description = "Start date", example = "2024-01-01")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "End date", example = "2024-01-31")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            
            @Parameter(description = "Locker ID (optional)")
            @RequestParam(required = false) Long lockerId) {
        
        // Generate report based on type
        ReportResponse report = generateReportByType(reportType, startDate, endDate, lockerId);
        
        // Export to PDF
        byte[] pdfData = reportService.exportToPdf(report);
        
        return ResponseEntity.ok()
            .header("Content-Type", "application/pdf")
            .header("Content-Disposition", "attachment; filename=\"report_" + reportType + ".pdf\"")
            .body(pdfData);
    }
    
    private ReportResponse generateReportByType(String reportType, LocalDate startDate, LocalDate endDate, Long lockerId) {
        return switch (reportType.toUpperCase()) {
            case "DEPOSITS", "DEPOSITS_BY_PERIOD" -> reportService.getDepositsByPeriod(startDate, endDate, lockerId);
            case "RETRIEVALS", "RETRIEVALS_BY_PERIOD" -> reportService.getRetrievalsByPeriod(startDate, endDate, lockerId);
            case "OCCUPANCY", "OCCUPANCY_RATE" -> reportService.getOccupancyRate(lockerId);
            case "EXPIRED_CODES" -> reportService.getExpiredCodes(startDate, endDate);
            case "COMPARTMENT_USAGE" -> reportService.getCompartmentUsage(lockerId);
            case "COURIER_PERFORMANCE" -> reportService.getCourierPerformance(lockerId, startDate, endDate);
            case "DAILY_SUMMARY" -> reportService.getDailySummary(startDate != null ? startDate : LocalDate.now());
            default -> throw new IllegalArgumentException("Unknown report type: " + reportType);
        };
    }
}
