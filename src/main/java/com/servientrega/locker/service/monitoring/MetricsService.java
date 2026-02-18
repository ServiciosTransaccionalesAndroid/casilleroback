package com.servientrega.locker.service.monitoring;

import com.servientrega.locker.entity.Compartment;
import com.servientrega.locker.enums.CompartmentSize;
import com.servientrega.locker.enums.CompartmentStatus;
import com.servientrega.locker.repository.CompartmentRepository;
import com.servientrega.locker.repository.DepositRepository;
import com.servientrega.locker.repository.RetrievalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricsService {

    private final CompartmentRepository compartmentRepository;
    private final DepositRepository depositRepository;
    private final RetrievalRepository retrievalRepository;

    public LockerMetrics getLockerMetrics(Long lockerId) {
        log.info("Getting metrics for locker: {}", lockerId);

        List<Compartment> compartments = compartmentRepository.findByLockerId(lockerId);
        
        long total = compartments.size();
        long available = compartments.stream()
            .filter(c -> c.getStatus() == CompartmentStatus.DISPONIBLE)
            .count();
        long occupied = compartments.stream()
            .filter(c -> c.getStatus() == CompartmentStatus.OCUPADO)
            .count();
        long maintenance = compartments.stream()
            .filter(c -> c.getStatus() == CompartmentStatus.MANTENIMIENTO)
            .count();

        double occupancyRate = total > 0 ? (double) occupied / total * 100 : 0;

        return new LockerMetrics(
            lockerId,
            (int) total,
            (int) available,
            (int) occupied,
            (int) maintenance,
            occupancyRate
        );
    }

    public OperationalMetrics getOperationalMetrics() {
        log.info("Getting operational metrics");

        long totalDeposits = depositRepository.count();
        long totalRetrievals = retrievalRepository.count();
        long pendingRetrievals = totalDeposits - totalRetrievals;

        return new OperationalMetrics(
            totalDeposits,
            totalRetrievals,
            pendingRetrievals
        );
    }

    public Map<String, Integer> getCompartmentUtilization(Long lockerId) {
        log.info("Getting compartment utilization for locker: {}", lockerId);

        List<Compartment> compartments = compartmentRepository.findByLockerId(lockerId);
        
        Map<String, Integer> utilization = new HashMap<>();
        
        for (CompartmentSize size : CompartmentSize.values()) {
            long occupied = compartments.stream()
                .filter(c -> c.getSize() == size && c.getStatus() == CompartmentStatus.OCUPADO)
                .count();
            long total = compartments.stream()
                .filter(c -> c.getSize() == size)
                .count();
            
            int percentage = total > 0 ? (int) ((double) occupied / total * 100) : 0;
            utilization.put(size.name(), percentage);
        }

        return utilization;
    }

    public record LockerMetrics(
        Long lockerId,
        int totalCompartments,
        int availableCompartments,
        int occupiedCompartments,
        int maintenanceCompartments,
        double occupancyRate
    ) {}

    public record OperationalMetrics(
        long totalDeposits,
        long totalRetrievals,
        long pendingRetrievals
    ) {}
}
