package com.servientrega.locker.dto;

import java.math.BigDecimal;

public record PackageValidationResponse(
    String trackingNumber,
    String recipientName,
    String recipientPhone,
    String recipientEmail,
    PackageDimensions dimensions,
    String status
) {
    public record PackageDimensions(
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth,
        BigDecimal weight
    ) {}
}
