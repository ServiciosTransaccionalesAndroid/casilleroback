package com.servientrega.locker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AdminAuthDTO {

    public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,
        
        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
    ) {}

    public record AuthResponse(
        String token,
        String email,
        String name,
        String role,
        String message
    ) {}
}
