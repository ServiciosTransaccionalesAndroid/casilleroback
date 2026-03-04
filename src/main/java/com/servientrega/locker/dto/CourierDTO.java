package com.servientrega.locker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CourierDTO {

    public record CreateRequest(
        @NotBlank(message = "Employee ID is required")
        String employeeId,
        
        @NotBlank(message = "Name is required")
        String name,
        
        String phone,
        
        @Email(message = "Invalid email format")
        String email,
        
        @NotBlank(message = "PIN is required")
        @Pattern(regexp = "^[0-9]{4,6}$", message = "PIN must be 4-6 digits")
        String pin
    ) {}

    public record UpdateRequest(
        String name,
        String phone,
        
        @Email(message = "Invalid email format")
        String email,
        
        @Pattern(regexp = "^[0-9]{4,6}$", message = "PIN must be 4-6 digits")
        String pin,
        
        Boolean active
    ) {}

    public record CourierResponse(
        Long id,
        String employeeId,
        String name,
        String phone,
        String email,
        String pin,
        Boolean active
    ) {}
}
