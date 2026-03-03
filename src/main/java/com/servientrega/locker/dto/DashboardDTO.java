package com.servientrega.locker.dto;

public class DashboardDTO {
    
    public record DashboardStats(
        TodayActivity todayActivity,
        CompartmentStats compartmentStats
    ) {}
    
    public record TodayActivity(
        int totalDeposits,
        int totalRetrievals,
        int pendingRetrievals
    ) {}
    
    public record CompartmentStats(
        int total,
        int available,
        int occupied,
        int maintenance,
        StatusBreakdown statusBreakdown,
        DoorBreakdown doorBreakdown,
        ConditionBreakdown conditionBreakdown
    ) {}
    
    public record StatusBreakdown(
        int disponible,
        int ocupado,
        int abierto,
        int mantenimiento
    ) {}
    
    public record DoorBreakdown(
        int cerrado,
        int abierto
    ) {}
    
    public record ConditionBreakdown(
        int buenEstado,
        int malEstado,
        int requiereMantenimiento
    ) {}
}
