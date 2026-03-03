package com.servientrega.locker.service;

import com.servientrega.locker.dto.DashboardDTO;
import com.servientrega.locker.enums.CompartmentStatus;
import com.servientrega.locker.enums.DoorState;
import com.servientrega.locker.enums.PhysicalCondition;
import com.servientrega.locker.repository.CompartmentRepository;
import com.servientrega.locker.repository.DepositRepository;
import com.servientrega.locker.repository.RetrievalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DepositRepository depositRepository;
    private final RetrievalRepository retrievalRepository;
    private final CompartmentRepository compartmentRepository;

    public DashboardDTO.DashboardStats getStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Actividad del día
        int depositsToday = depositRepository.countByDepositTimestampBetween(startOfDay, endOfDay);
        int retrievalsToday = retrievalRepository.countByRetrievalTimestampBetween(startOfDay, endOfDay);
        int pendingRetrievals = (int) depositRepository.count() - (int) retrievalRepository.count();

        DashboardDTO.TodayActivity todayActivity = new DashboardDTO.TodayActivity(
            depositsToday,
            retrievalsToday,
            Math.max(0, pendingRetrievals)
        );

        // Estadísticas de compartimentos
        var allCompartments = compartmentRepository.findAll();
        int total = allCompartments.size();
        
        long disponible = allCompartments.stream().filter(c -> c.getStatus() == CompartmentStatus.DISPONIBLE).count();
        long ocupado = allCompartments.stream().filter(c -> c.getStatus() == CompartmentStatus.OCUPADO).count();
        long abierto = allCompartments.stream().filter(c -> c.getStatus() == CompartmentStatus.ABIERTO).count();
        long mantenimiento = allCompartments.stream().filter(c -> c.getStatus() == CompartmentStatus.MANTENIMIENTO).count();

        long cerrado = allCompartments.stream().filter(c -> c.getDoorState() == DoorState.CERRADO).count();
        long puertaAbierta = allCompartments.stream().filter(c -> c.getDoorState() == DoorState.ABIERTO).count();

        long buenEstado = allCompartments.stream().filter(c -> c.getPhysicalCondition() == PhysicalCondition.BUEN_ESTADO).count();
        long malEstado = allCompartments.stream().filter(c -> c.getPhysicalCondition() == PhysicalCondition.MAL_ESTADO).count();
        long requiereMantenimiento = allCompartments.stream().filter(c -> c.getPhysicalCondition() == PhysicalCondition.REQUIERE_MANTENIMIENTO).count();

        DashboardDTO.CompartmentStats compartmentStats = new DashboardDTO.CompartmentStats(
            total,
            (int) disponible,
            (int) ocupado,
            (int) mantenimiento,
            new DashboardDTO.StatusBreakdown((int) disponible, (int) ocupado, (int) abierto, (int) mantenimiento),
            new DashboardDTO.DoorBreakdown((int) cerrado, (int) puertaAbierta),
            new DashboardDTO.ConditionBreakdown((int) buenEstado, (int) malEstado, (int) requiereMantenimiento)
        );

        return new DashboardDTO.DashboardStats(todayActivity, compartmentStats);
    }
}
