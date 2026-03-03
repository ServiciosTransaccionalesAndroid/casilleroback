package com.servientrega.locker.controller;

import com.servientrega.locker.dto.CompartmentDTO;
import com.servientrega.locker.entity.Compartment;
import com.servientrega.locker.enums.DoorState;
import com.servientrega.locker.enums.PhysicalCondition;
import com.servientrega.locker.service.CompartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/compartments")
@RequiredArgsConstructor
@Tag(name = "Compartments", description = "Gestión de estados de compartimentos")
public class CompartmentController {

    private final CompartmentService compartmentService;

    @GetMapping("/{id}")
    @Operation(summary = "Obtener compartimento por ID")
    public ResponseEntity<CompartmentDTO.CompartmentResponse> getCompartment(@PathVariable Long id) {
        Compartment compartment = compartmentService.getCompartmentById(id);
        return ResponseEntity.ok(toResponse(compartment));
    }

    @GetMapping("/locker/{lockerId}")
    @Operation(summary = "Listar compartimentos de un locker")
    public ResponseEntity<List<CompartmentDTO.CompartmentResponse>> getCompartmentsByLocker(
            @PathVariable Long lockerId) {
        List<Compartment> compartments = compartmentService.getCompartmentsByLocker(lockerId);
        return ResponseEntity.ok(compartments.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}/door-state")
    @Operation(summary = "Actualizar estado de puerta", description = "Valores: CERRADO, ABIERTO")
    public ResponseEntity<String> updateDoorState(
            @PathVariable Long id,
            @RequestBody CompartmentDTO.UpdateDoorStateRequest request) {
        DoorState doorState = DoorState.valueOf(request.doorState());
        compartmentService.updateDoorState(id, doorState);
        return ResponseEntity.ok("Door state updated to " + doorState);
    }

    @PutMapping("/{id}/physical-condition")
    @Operation(summary = "Actualizar condición física", 
               description = "Valores: BUEN_ESTADO, MAL_ESTADO, REQUIERE_MANTENIMIENTO")
    public ResponseEntity<String> updatePhysicalCondition(
            @PathVariable Long id,
            @RequestBody CompartmentDTO.UpdatePhysicalConditionRequest request) {
        PhysicalCondition condition = PhysicalCondition.valueOf(request.physicalCondition());
        compartmentService.updatePhysicalCondition(id, condition);
        return ResponseEntity.ok("Physical condition updated to " + condition);
    }

    private CompartmentDTO.CompartmentResponse toResponse(Compartment c) {
        return new CompartmentDTO.CompartmentResponse(
                c.getId(),
                c.getCompartmentNumber(),
                c.getSize().name(),
                c.getStatus().name(),
                c.getDoorState().name(),
                c.getPhysicalCondition().name(),
                c.getLocker().getId()
        );
    }
}
