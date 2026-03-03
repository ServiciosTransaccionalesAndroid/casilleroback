package com.servientrega.locker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RecipientDTO {

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
