package com.servientrega.locker.service.monitoring;

import com.servientrega.locker.entity.Compartment;
import com.servientrega.locker.enums.AlertSeverity;
import com.servientrega.locker.enums.AlertType;
import com.servientrega.locker.enums.CompartmentStatus;
import com.servientrega.locker.repository.CompartmentRepository;
import com.servientrega.locker.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaintenanceService {

    private final AlertService alertService;
    private final CompartmentRepository compartmentRepository;
    private final NotificationService notificationService;

    public void detectMaintenanceNeeded(Long lockerId, Long compartmentId, 
                                       String currentState, Map<String, Object> sensorReadings) {
        log.info("Detecting maintenance needs for compartment: {}", compartmentId);

        if ("MANTENIMIENTO".equals(currentState)) {
            log.warn("Compartment {} requires maintenance", compartmentId);
            scheduleMaintenanceTicket(lockerId, compartmentId, "Compartment in maintenance state");
        }

        if (sensorReadings != null && hasSensorError(sensorReadings)) {
            log.warn("Sensor error detected in compartment {}", compartmentId);
            scheduleMaintenanceTicket(lockerId, compartmentId, "Sensor error detected");
        }
    }

    private boolean hasSensorError(Map<String, Object> sensorReadings) {
        if (sensorReadings.containsKey("error") && Boolean.TRUE.equals(sensorReadings.get("error"))) {
            return true;
        }
        
        long trueCount = sensorReadings.values().stream()
            .filter(v -> v instanceof Boolean && (Boolean) v)
            .count();
        
        return trueCount == 1 || trueCount == 2;
    }

    public void scheduleMaintenanceTicket(Long lockerId, Long compartmentId, String issue) {
        log.info("Scheduling maintenance ticket for compartment: {}", compartmentId);

        alertService.createAlert(
            lockerId,
            compartmentId,
            AlertType.MAINTENANCE,
            AlertSeverity.CRITICAL,
            issue
        );

        Compartment compartment = compartmentRepository.findById(compartmentId).orElse(null);
        if (compartment != null) {
            notificationService.sendMaintenanceAlert(
                "maintenance@servientrega.com",
                compartment.getLocker().getName(),
                String.valueOf(compartment.getCompartmentNumber()),
                issue
            );
        }

        log.info("Maintenance ticket scheduled successfully");
    }

    public List<Compartment> getCompartmentsInMaintenance(Long lockerId) {
        log.info("Getting compartments in maintenance for locker: {}", lockerId);
        return compartmentRepository.findByLockerIdAndStatus(lockerId, CompartmentStatus.MANTENIMIENTO);
    }
}
