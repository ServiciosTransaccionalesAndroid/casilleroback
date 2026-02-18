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
        
        @NotNull(message = "Compartment ID is required")
        Long compartmentId,
        
        @NotNull(message = "Courier ID is required")
        Long courierId,
        
        String photoUrl
    ) {}

    public record DepositResponse(
        Long depositId,
        String retrievalCode,
        String secretPin,
        LocalDateTime expiresAt,
        String message
    ) {}
}
