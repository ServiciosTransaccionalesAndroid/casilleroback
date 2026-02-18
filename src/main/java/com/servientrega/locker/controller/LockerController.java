package com.servientrega.locker.controller;

import com.servientrega.locker.dto.LockerDTO;
import com.servientrega.locker.entity.Compartment;
import com.servientrega.locker.entity.Locker;
import com.servientrega.locker.enums.CompartmentStatus;
import com.servientrega.locker.repository.LockerRepository;
import com.servientrega.locker.service.CompartmentService;
import com.servientrega.locker.service.StatusHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/lockers")
@RequiredArgsConstructor
@Tag(name = "Lockers", description = "Locker management endpoints")
public class LockerController {

    private final CompartmentService compartmentService;
    private final StatusHistoryService statusHistoryService;
    private final LockerRepository lockerRepository;

    @PostMapping("/status-update")
    @Operation(summary = "Update compartment status", description = "Updates compartment status from proprietary software")
    public ResponseEntity<LockerDTO.StatusUpdateResponse> updateStatus(
            @Valid @RequestBody LockerDTO.StatusUpdateRequest request) {
        
        statusHistoryService.recordStatusChange(
            request.compartmentId(),
            request.previousState(),
            request.currentState(),
            request.sensorReadings()
        );

        LockerDTO.StatusUpdateResponse response = new LockerDTO.StatusUpdateResponse(
            "Status updated successfully",
            LocalDateTime.now()
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{lockerId}/status")
    @Operation(summary = "Get locker status", description = "Gets overall locker status")
    public ResponseEntity<LockerDTO.LockerStatusResponse> getLockerStatus(
            @PathVariable Long lockerId) {
        
        Locker locker = lockerRepository.findById(lockerId)
            .orElseThrow(() -> new RuntimeException("Locker not found: " + lockerId));

        List<Compartment> compartments = compartmentService.getCompartmentsByLocker(lockerId);
        
        long available = compartments.stream()
            .filter(c -> c.getStatus() == CompartmentStatus.DISPONIBLE)
            .count();
        
        long occupied = compartments.stream()
            .filter(c -> c.getStatus() == CompartmentStatus.OCUPADO)
            .count();

        LockerDTO.LockerStatusResponse response = new LockerDTO.LockerStatusResponse(
            locker.getId(),
            locker.getName(),
            locker.getLocation(),
            locker.getStatus().name(),
            compartments.size(),
            (int) available,
            (int) occupied
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{lockerId}/compartments")
    @Operation(summary = "List compartments", description = "Lists all compartments for a locker")
    public ResponseEntity<List<LockerDTO.CompartmentInfo>> getCompartments(
            @PathVariable Long lockerId) {
        
        List<Compartment> compartments = compartmentService.getCompartmentsByLocker(lockerId);
        
        List<LockerDTO.CompartmentInfo> response = compartments.stream()
            .map(c -> new LockerDTO.CompartmentInfo(
                c.getId(),
                c.getCompartmentNumber(),
                c.getSize().name(),
                c.getStatus().name()
            ))
            .toList();

        return ResponseEntity.ok(response);
    }
}
