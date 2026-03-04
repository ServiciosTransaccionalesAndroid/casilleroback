package com.servientrega.locker.service;

import com.servientrega.locker.entity.Compartment;
import com.servientrega.locker.enums.CompartmentSize;
import com.servientrega.locker.enums.CompartmentStatus;
import com.servientrega.locker.enums.DoorState;
import com.servientrega.locker.enums.PhysicalCondition;
import com.servientrega.locker.repository.CompartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompartmentService {

    private final CompartmentRepository compartmentRepository;

    public List<Compartment> findAvailableCompartments(Long lockerId) {
        log.info("Finding available compartments for locker: {}", lockerId);
        return compartmentRepository.findByLockerIdAndStatus(lockerId, CompartmentStatus.DISPONIBLE);
    }

    public Compartment assignCompartment(Long lockerId, BigDecimal width, BigDecimal height, BigDecimal depth) {
        log.info("Assigning compartment for package dimensions: {}x{}x{}", width, height, depth);
        
        List<Compartment> availableCompartments = findAvailableCompartments(lockerId);
        
        // Intentar asignar en orden: S_SMALL -> M_SMALL -> MEDIUM -> LARGE
        for (CompartmentSize size : CompartmentSize.values()) {
            if (size.canFit(width, height, depth)) {
                log.info("Package fits in size: {}", size);
                
                Compartment assigned = availableCompartments.stream()
                    .filter(c -> c.getSize() == size)
                    .findFirst()
                    .orElse(null);
                
                if (assigned != null) {
                    log.info("Compartment assigned: {} (Size: {})", assigned.getId(), assigned.getSize());
                    return assigned;
                }
                
                log.info("No available compartment of size {}, trying next size", size);
            }
        }
        
        log.warn("No available compartment found for package dimensions: {}x{}x{}", width, height, depth);
        return null;
    }


    @Transactional
    public void updateCompartmentStatus(Long compartmentId, CompartmentStatus status) {
        log.info("Updating compartment {} to status: {}", compartmentId, status);
        
        Compartment compartment = compartmentRepository.findById(compartmentId)
            .orElseThrow(() -> new RuntimeException("Compartment not found: " + compartmentId));
        
        compartment.setStatus(status);
        compartmentRepository.save(compartment);
        
        log.info("Compartment status updated successfully");
    }

    public List<Compartment> getCompartmentsByLocker(Long lockerId) {
        log.info("Getting all compartments for locker: {}", lockerId);
        return compartmentRepository.findByLockerId(lockerId);
    }

    @Transactional
    public void updateDoorState(Long compartmentId, DoorState doorState) {
        Compartment compartment = compartmentRepository.findById(compartmentId)
            .orElseThrow(() -> new RuntimeException("Compartment not found"));
        compartment.setDoorState(doorState);
        compartmentRepository.save(compartment);
    }

    @Transactional
    public void updateDoorStateByNumber(Long lockerId, Integer compartmentNumber, DoorState doorState) {
        Compartment compartment = compartmentRepository.findByLockerIdAndCompartmentNumber(lockerId, compartmentNumber);
        if (compartment == null) {
            throw new RuntimeException("Compartment not found: Locker " + lockerId + ", Number " + compartmentNumber);
        }
        compartment.setDoorState(doorState);
        compartmentRepository.save(compartment);
    }

    @Transactional
    public void updatePhysicalCondition(Long compartmentId, PhysicalCondition condition) {
        Compartment compartment = compartmentRepository.findById(compartmentId)
            .orElseThrow(() -> new RuntimeException("Compartment not found"));
        compartment.setPhysicalCondition(condition);
        compartmentRepository.save(compartment);
    }

    public Compartment getCompartmentById(Long compartmentId) {
        return compartmentRepository.findById(compartmentId)
            .orElseThrow(() -> new RuntimeException("Compartment not found"));
    }
}
