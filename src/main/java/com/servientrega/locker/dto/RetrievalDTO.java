package com.servientrega.locker.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class RetrievalDTO {

    public record RetrievalValidationResponse(
        boolean valid,
        Long compartmentId,
        String trackingNumber,
        LocalDateTime expiresAt,
        String message
    ) {}

    public record RetrievalRequest(
        @NotBlank(message = "Code is required")
        String code,
        
        String photoUrl
    ) {}

    public record RetrievalResponse(
        Long retrievalId,
        LocalDateTime timestamp,
        String message
    ) {}
}
