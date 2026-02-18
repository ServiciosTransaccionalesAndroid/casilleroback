package com.servientrega.locker.service.monitoring;

import com.servientrega.locker.entity.Alert;
import com.servientrega.locker.entity.Compartment;
import com.servientrega.locker.entity.Locker;
import com.servientrega.locker.enums.AlertSeverity;
import com.servientrega.locker.enums.AlertType;
import com.servientrega.locker.repository.AlertRepository;
import com.servientrega.locker.repository.CompartmentRepository;
import com.servientrega.locker.repository.LockerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;
    private final LockerRepository lockerRepository;
    private final CompartmentRepository compartmentRepository;

    @Transactional
    public Alert createAlert(Long lockerId, Long compartmentId, AlertType alertType, 
                            AlertSeverity severity, String message) {
        log.info("Creating alert - Locker: {}, Type: {}, Severity: {}", lockerId, alertType, severity);

        Locker locker = lockerRepository.findById(lockerId)
            .orElseThrow(() -> new RuntimeException("Locker not found: " + lockerId));

        Compartment compartment = null;
        if (compartmentId != null) {
            compartment = compartmentRepository.findById(compartmentId)
                .orElseThrow(() -> new RuntimeException("Compartment not found: " + compartmentId));
        }

        Alert alert = new Alert();
        alert.setLocker(locker);
        alert.setCompartment(compartment);
        alert.setAlertType(alertType);
        alert.setSeverity(severity);
        alert.setMessage(message);

        Alert savedAlert = alertRepository.save(alert);
        log.info("Alert created with ID: {}", savedAlert.getId());
        return savedAlert;
    }

    public List<Alert> getActiveAlerts(Long lockerId) {
        log.info("Getting active alerts for locker: {}", lockerId);
        return alertRepository.findByLockerIdAndResolvedAtIsNull(lockerId);
    }

    @Transactional
    public void resolveAlert(Long alertId) {
        log.info("Resolving alert: {}", alertId);
        
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
        
        alert.setResolvedAt(LocalDateTime.now());
        alertRepository.save(alert);
        
        log.info("Alert resolved successfully");
    }
}
