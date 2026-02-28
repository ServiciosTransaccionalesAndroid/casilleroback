package com.servientrega.locker.service;

import com.servientrega.locker.dto.ReportResponse;
import com.servientrega.locker.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final ReportRepository reportRepository;
    private final CsvExportService csvExportService;
    private final PdfExportService pdfExportService;

    public ReportResponse getDepositsByPeriod(LocalDate startDate, LocalDate endDate, Long lockerId) {
        log.info("Generating deposits report from {} to {}, lockerId: {}", startDate, endDate, lockerId);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Map<String, Object>> dailyData = reportRepository.getDepositsByDay(start, end, lockerId);
        
        // Calcular resumen
        long totalDeposits = dailyData.stream()
            .mapToLong(row -> ((Number) row.get("count")).longValue())
            .sum();
        
        double averagePerDay = dailyData.isEmpty() ? 0 : (double) totalDeposits / dailyData.size();
        
        Map<String, Object> peakDay = dailyData.stream()
            .max(Comparator.comparingLong(row -> ((Number) row.get("count")).longValue()))
            .orElse(null);
        
        Map<String, Object> data = new HashMap<>();
        data.put("daily", dailyData);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDeposits", totalDeposits);
        summary.put("averagePerDay", Math.round(averagePerDay * 100.0) / 100.0);
        summary.put("daysWithData", dailyData.size());
        if (peakDay != null) {
            summary.put("peakDay", peakDay.get("date"));
            summary.put("peakDayCount", peakDay.get("count"));
        }
        
        return new ReportResponse(
            "DEPOSITS_BY_PERIOD",
            startDate,
            endDate,
            data,
            summary,
            LocalDateTime.now()
        );
    }

    public ReportResponse getRetrievalsByPeriod(LocalDate startDate, LocalDate endDate, Long lockerId) {
        log.info("Generating retrievals report from {} to {}, lockerId: {}", startDate, endDate, lockerId);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Map<String, Object>> dailyData = reportRepository.getRetrievalsByDay(start, end, lockerId);
        
        long totalRetrievals = dailyData.stream()
            .mapToLong(row -> ((Number) row.get("count")).longValue())
            .sum();
        
        double averagePerDay = dailyData.isEmpty() ? 0 : (double) totalRetrievals / dailyData.size();
        
        Map<String, Object> peakDay = dailyData.stream()
            .max(Comparator.comparingLong(row -> ((Number) row.get("count")).longValue()))
            .orElse(null);
        
        Map<String, Object> data = new HashMap<>();
        data.put("daily", dailyData);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalRetrievals", totalRetrievals);
        summary.put("averagePerDay", Math.round(averagePerDay * 100.0) / 100.0);
        summary.put("daysWithData", dailyData.size());
        if (peakDay != null) {
            summary.put("peakDay", peakDay.get("date"));
            summary.put("peakDayCount", peakDay.get("count"));
        }
        
        return new ReportResponse(
            "RETRIEVALS_BY_PERIOD",
            startDate,
            endDate,
            data,
            summary,
            LocalDateTime.now()
        );
    }

    public ReportResponse getOccupancyRate(Long lockerId) {
        log.info("Generating occupancy report for locker: {}", lockerId);
        
        List<Map<String, Object>> occupancyData = reportRepository.getOccupancyBySize(lockerId);
        
        long totalCompartments = occupancyData.stream()
            .mapToLong(row -> ((Number) row.get("total")).longValue())
            .sum();
        
        long totalOccupied = occupancyData.stream()
            .mapToLong(row -> ((Number) row.get("occupied")).longValue())
            .sum();
        
        long totalAvailable = occupancyData.stream()
            .mapToLong(row -> ((Number) row.get("available")).longValue())
            .sum();
        
        double occupancyRate = totalCompartments > 0 
            ? (double) totalOccupied / totalCompartments * 100 
            : 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("bySize", occupancyData);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCompartments", totalCompartments);
        summary.put("occupied", totalOccupied);
        summary.put("available", totalAvailable);
        summary.put("occupancyRate", Math.round(occupancyRate * 100.0) / 100.0);
        
        return new ReportResponse(
            "OCCUPANCY_RATE",
            LocalDate.now(),
            LocalDate.now(),
            data,
            summary,
            LocalDateTime.now()
        );
    }

    public ReportResponse getExpiredCodes(LocalDate startDate, LocalDate endDate) {
        log.info("Generating expired codes report from {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Map<String, Object>> expiredCodes = reportRepository.getExpiredCodes(start, end);
        
        Map<String, Object> data = new HashMap<>();
        data.put("expiredCodes", expiredCodes);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalExpired", expiredCodes.size());
        
        return new ReportResponse(
            "EXPIRED_CODES",
            startDate,
            endDate,
            data,
            summary,
            LocalDateTime.now()
        );
    }
    
    public ReportResponse getCourierPerformance(Long courierId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating courier performance report for courier: {} from {} to {}", 
            courierId, startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Map<String, Object>> performanceData = reportRepository.getCourierPerformance(start, end);
        
        // Filtrar por courier si se especifica
        if (courierId != null) {
            performanceData = performanceData.stream()
                .filter(row -> courierId.equals(((Number) row.get("courierId")).longValue()))
                .toList();
        }
        
        long totalDeposits = performanceData.stream()
            .mapToLong(row -> ((Number) row.get("totalDeposits")).longValue())
            .sum();
        
        double averagePerDay = performanceData.isEmpty() ? 0 : 
            (double) totalDeposits / performanceData.stream()
                .map(row -> row.get("date"))
                .distinct()
                .count();
        
        Map<String, Object> data = new HashMap<>();
        data.put("daily", performanceData);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDeposits", totalDeposits);
        summary.put("averagePerDay", Math.round(averagePerDay * 100.0) / 100.0);
        summary.put("daysActive", performanceData.stream().map(r -> r.get("date")).distinct().count());
        
        return new ReportResponse(
            "COURIER_PERFORMANCE",
            startDate,
            endDate,
            data,
            summary,
            LocalDateTime.now()
        );
    }
    
    public ReportResponse getDepositsByCourier(LocalDate startDate, LocalDate endDate) {
        log.info("Generating deposits by courier report from {} to {}", startDate, endDate);
        
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);
        
        List<Map<String, Object>> courierData = reportRepository.getDepositsByCourier(start, end);
        
        long totalDeposits = courierData.stream()
            .mapToLong(row -> ((Number) row.get("totalDeposits")).longValue())
            .sum();
        
        Map<String, Object> data = new HashMap<>();
        data.put("byCourier", courierData);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalDeposits", totalDeposits);
        summary.put("totalCouriers", courierData.size());
        
        return new ReportResponse(
            "DEPOSITS_BY_COURIER",
            startDate,
            endDate,
            data,
            summary,
            LocalDateTime.now()
        );
    }
    
    public ReportResponse getCompartmentUsage(Long lockerId) {
        log.info("Generating compartment usage report for locker: {}", lockerId);
        
        List<Map<String, Object>> usageData = reportRepository.getCompartmentUsage(lockerId);
        
        long totalCompartments = usageData.size();
        long totalUsage = usageData.stream()
            .mapToLong(row -> ((Number) row.get("timesUsed")).longValue())
            .sum();
        
        double averageUsage = totalCompartments > 0 ? (double) totalUsage / totalCompartments : 0;
        
        Map<String, Object> data = new HashMap<>();
        data.put("compartments", usageData);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCompartments", totalCompartments);
        summary.put("totalUsage", totalUsage);
        summary.put("averageUsagePerCompartment", Math.round(averageUsage * 100.0) / 100.0);
        
        return new ReportResponse(
            "COMPARTMENT_USAGE",
            LocalDate.now(),
            LocalDate.now(),
            data,
            summary,
            LocalDateTime.now()
        );
    }
    
    public ReportResponse getActivePackages(Long lockerId) {
        log.info("Generating active packages report for locker: {}", lockerId);
        
        List<Map<String, Object>> activePackages = reportRepository.getActivePackages(lockerId);
        
        Map<String, Object> data = new HashMap<>();
        data.put("packages", activePackages);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalActive", activePackages.size());
        
        return new ReportResponse(
            "ACTIVE_PACKAGES",
            LocalDate.now(),
            LocalDate.now(),
            data,
            summary,
            LocalDateTime.now()
        );
    }
    
    public ReportResponse getDailySummary(LocalDate date) {
        log.info("Generating daily summary for date: {}", date);
        
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        
        // Obtener datos del día
        List<Map<String, Object>> deposits = reportRepository.getDepositsByDay(start, end, null);
        List<Map<String, Object>> retrievals = reportRepository.getRetrievalsByDay(start, end, null);
        List<Map<String, Object>> courierActivity = reportRepository.getDepositsByCourier(start, end);
        
        long totalDeposits = deposits.stream()
            .mapToLong(row -> ((Number) row.get("count")).longValue())
            .sum();
        
        long totalRetrievals = retrievals.stream()
            .mapToLong(row -> ((Number) row.get("count")).longValue())
            .sum();
        
        Map<String, Object> data = new HashMap<>();
        data.put("deposits", deposits);
        data.put("retrievals", retrievals);
        data.put("courierActivity", courierActivity);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("date", date);
        summary.put("totalDeposits", totalDeposits);
        summary.put("totalRetrievals", totalRetrievals);
        summary.put("activeCouriers", courierActivity.size());
        summary.put("pendingPackages", totalDeposits - totalRetrievals);
        
        return new ReportResponse(
            "DAILY_SUMMARY",
            date,
            date,
            data,
            summary,
            LocalDateTime.now()
        );
    }
    
    public byte[] exportToCsv(ReportResponse report) {
        log.info("Exporting report to CSV: {}", report.reportType());
        return csvExportService.exportReportToCsv(report);
    }
    
    public byte[] exportToPdf(ReportResponse report) {
        log.info("Exporting report to PDF: {}", report.reportType());
        return pdfExportService.exportReportToPdf(report);
    }
}
