package com.servientrega.locker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.Map;

public class LockerDTO {

    public record StatusUpdateRequest(
        @NotNull(message = "Locker ID is required")
        Long lockerId,
        
        @NotNull(message = "Compartment ID is required")
        Long compartmentId,
        
        @NotBlank(message = "Previous state is required")
        String previousState,
        
        @NotBlank(message = "Current state is required")
        String currentState,
        
        LocalDateTime timestamp,
        
        Map<String, Object> sensorReadings
    ) {}

    public record StatusUpdateResponse(
        String message,
        LocalDateTime timestamp
    ) {}

    public record LockerStatusResponse(
        Long lockerId,
        String name,
        String location,
        String status,
        int totalCompartments,
        int availableCompartments,
        int occupiedCompartments
    ) {}

    public record CompartmentInfo(
        Long id,
        Integer compartmentNumber,
        String size,
        String status
    ) {}
}
