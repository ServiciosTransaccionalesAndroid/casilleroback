package com.servientrega.locker.service;

import com.servientrega.locker.entity.Compartment;
import com.servientrega.locker.entity.StatusHistory;
import com.servientrega.locker.repository.CompartmentRepository;
import com.servientrega.locker.repository.StatusHistoryRepository;
import com.servientrega.locker.service.monitoring.MaintenanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatusHistoryService {

    private final StatusHistoryRepository statusHistoryRepository;
    private final CompartmentRepository compartmentRepository;
    private final MaintenanceService maintenanceService;

    @Transactional
    public void recordStatusChange(Long compartmentId, String previousState, String currentState, 
                                   Map<String, Object> sensorReadings) {
        log.info("Recording status change for compartment {}: {} -> {}", 
            compartmentId, previousState, currentState);

        Compartment compartment = compartmentRepository.findById(compartmentId)
            .orElseThrow(() -> new RuntimeException("Compartment not found: " + compartmentId));

        StatusHistory history = new StatusHistory();
        history.setCompartment(compartment);
        history.setPreviousState(previousState);
        history.setCurrentState(currentState);
        history.setTimestamp(LocalDateTime.now());
        history.setSensorReadings(sensorReadings);

        statusHistoryRepository.save(history);
        log.info("Status change recorded successfully");

        maintenanceService.detectMaintenanceNeeded(
            compartment.getLocker().getId(),
            compartmentId,
            currentState,
            sensorReadings
        );
    }

    public List<StatusHistory> getCompartmentHistory(Long compartmentId) {
        log.info("Getting history for compartment: {}", compartmentId);
        return statusHistoryRepository.findByCompartmentIdOrderByTimestampDesc(compartmentId);
    }
}
