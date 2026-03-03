package com.servientrega.locker.dto;

import jakarta.validation.constraints.Email;
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
        @Positive BigDecimal weight
    ) {}

    public record UpdateRequest(
        Long recipientId,
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth,
        BigDecimal weight,
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
        String status
    ) {}
}

class RecipientDTO {

    public record CreateRequest(
        @NotBlank(message = "Name is required")
        String name,
        
        @NotBlank(message = "Phone is required")
        String phone,
        
        @Email String email,
        String address
    ) {}

    public record UpdateRequest(
        String name,
        String phone,
        @Email String email,
        String address
    ) {}

    public record RecipientResponse(
        Long id,
        String name,
        String phone,
        String email,
        String address
    ) {}
}
