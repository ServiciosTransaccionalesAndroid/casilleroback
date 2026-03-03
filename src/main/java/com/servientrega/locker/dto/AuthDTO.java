package com.servientrega.locker.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDTO {

    public record LoginRequest(
        @NotBlank(message = "Employee ID is required")
        String employeeId,
        
        @NotBlank(message = "PIN is required")
        @jakarta.validation.constraints.Pattern(
            regexp = "^[0-9]{4,6}$",
            message = "PIN must be 4-6 digits"
        )
        String pin
    ) {}

    public record AuthResponse(
        String token,
        String employeeId,
        String name,
        String message
    ) {}
}
