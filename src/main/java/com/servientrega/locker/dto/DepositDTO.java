package com.servientrega.locker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class DepositDTO {

    public record DepositRequest(
        @NotBlank(message = "Tracking number is required")
        String trackingNumber,
        
        @NotNull(message = "Locker ID is required")
        Long lockerId,
        
        @NotBlank(message = "Courier employee ID is required")
        String courierEmployeeId,
        
        String photoUrl
    ) {}

    public record DepositResponse(
        Long depositId,
        Integer compartmentNumber,
        String retrievalCode,
        String secretPin,
        LocalDateTime expiresAt,
        String message
    ) {}
}
