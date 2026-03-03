package com.servientrega.locker.dto;

public class CompartmentDTO {
    
    public record UpdateDoorStateRequest(String doorState) {}
    
    public record UpdatePhysicalConditionRequest(String physicalCondition) {}
    
    public record CompartmentResponse(
        Long id,
        Integer compartmentNumber,
        String size,
        String status,
        String doorState,
        String physicalCondition,
        Long lockerId
    ) {}
}
