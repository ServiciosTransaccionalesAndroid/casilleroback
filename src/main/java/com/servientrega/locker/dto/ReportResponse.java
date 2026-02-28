package com.servientrega.locker.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

public record ReportResponse(
    String reportType,
    LocalDate startDate,
    LocalDate endDate,
    Map<String, Object> data,
    Map<String, Object> summary,
    LocalDateTime generatedAt
) {}
