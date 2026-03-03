package com.servientrega.locker.controller;

import com.servientrega.locker.dto.CourierDTO;
import com.servientrega.locker.service.CourierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
@Tag(name = "Couriers", description = "Courier management endpoints")
public class CourierController {

    private final CourierService courierService;

    @PostMapping
    @Operation(summary = "Create courier", description = "Create a new courier with PIN")
    public ResponseEntity<CourierDTO.CourierResponse> create(
            @Valid @RequestBody CourierDTO.CreateRequest request) {
        CourierDTO.CourierResponse response = courierService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all couriers", description = "Get all couriers")
    public ResponseEntity<List<CourierDTO.CourierResponse>> getAll() {
        return ResponseEntity.ok(courierService.getAll());
    }

    @GetMapping("/{employeeId}")
    @Operation(summary = "Get courier", description = "Get courier by employee ID")
    public ResponseEntity<CourierDTO.CourierResponse> getByEmployeeId(
            @PathVariable String employeeId) {
        return ResponseEntity.ok(courierService.getByEmployeeId(employeeId));
    }

    @PutMapping("/{employeeId}")
    @Operation(summary = "Update courier", description = "Update courier information including PIN")
    public ResponseEntity<CourierDTO.CourierResponse> update(
            @PathVariable String employeeId,
            @Valid @RequestBody CourierDTO.UpdateRequest request) {
        return ResponseEntity.ok(courierService.update(employeeId, request));
    }

    @DeleteMapping("/{employeeId}")
    @Operation(summary = "Delete courier", description = "Delete courier by employee ID")
    public ResponseEntity<Void> delete(@PathVariable String employeeId) {
        courierService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}
