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
        
        CompartmentSize requiredSize = calculateRequiredSize(width, height, depth);
        log.info("Required compartment size: {}", requiredSize);
        
        List<Compartment> availableCompartments = findAvailableCompartments(lockerId);
        
        Compartment assigned = availableCompartments.stream()
            .filter(c -> c.getSize() == requiredSize)
            .findFirst()
            .orElseGet(() -> availableCompartments.stream()
                .filter(c -> isLargerSize(c.getSize(), requiredSize))
                .findFirst()
                .orElse(null));
        
        if (assigned != null) {
            log.info("Compartment assigned: {} (Size: {})", assigned.getId(), assigned.getSize());
        } else {
            log.warn("No available compartment found for size: {}", requiredSize);
        }
        
        return assigned;
    }

    private CompartmentSize calculateRequiredSize(BigDecimal width, BigDecimal height, BigDecimal depth) {
        BigDecimal maxDimension = width.max(height).max(depth);
        
        if (maxDimension.compareTo(new BigDecimal("25")) <= 0) {
            return CompartmentSize.SMALL;
        } else if (maxDimension.compareTo(new BigDecimal("40")) <= 0) {
            return CompartmentSize.MEDIUM;
        } else {
            return CompartmentSize.LARGE;
        }
    }

    private boolean isLargerSize(CompartmentSize compartmentSize, CompartmentSize requiredSize) {
        int compartmentOrder = getSizeOrder(compartmentSize);
        int requiredOrder = getSizeOrder(requiredSize);
        return compartmentOrder > requiredOrder;
    }

    private int getSizeOrder(CompartmentSize size) {
        return switch (size) {
            case SMALL -> 1;
            case MEDIUM -> 2;
            case LARGE -> 3;
        };
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
