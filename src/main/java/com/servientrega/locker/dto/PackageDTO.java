package com.servientrega.locker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PackageDTO {

    public record CreateRequest(
        @NotBlank(message = "Tracking number is required")
        String trackingNumber,
        
        @NotNull(message = "Recipient ID is required")
        Long recipientId,
        
        @Positive BigDecimal width,
        @Positive BigDecimal height,
        @Positive BigDecimal depth,
        @Positive BigDecimal weight,
        
        String description
    ) {}

    public record UpdateRequest(
        Long recipientId,
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth,
        BigDecimal weight,
        String description,
        String status
    ) {}

    public record PackageResponse(
        Long id,
        String trackingNumber,
        RecipientDTO.RecipientResponse recipient,
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth,
        BigDecimal weight,
        String description,
        String status
    ) {}
}
