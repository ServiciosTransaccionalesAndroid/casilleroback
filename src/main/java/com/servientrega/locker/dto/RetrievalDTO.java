package com.servientrega.locker.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class RetrievalDTO {

    public record RetrievalValidationResponse(
        boolean valid,
        Integer compartmentNumber,
        String trackingNumber,
        LocalDateTime expiresAt,
        String message
    ) {}

    public record RetrievalRequest(
        @NotBlank(message = "Code is required")
        String code,
        
        @NotBlank(message = "Secret PIN is required")
        String secretPin,
        
        String photoUrl
    ) {}

    public record RetrievalResponse(
        Long retrievalId,
        LocalDateTime timestamp,
        String message
    ) {}

    public record RetrievalCodeInfo(
        String code,
        String secretPin,
        String trackingNumber,
        Integer compartmentNumber,
        String lockerName,
        LocalDateTime expiresAt,
        boolean used
    ) {}
}
