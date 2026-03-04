package com.servientrega.locker.controller;

import com.servientrega.locker.dto.PackageDTO;
import com.servientrega.locker.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Tag(name = "Packages", description = "Package management endpoints")
public class PackageController {

    private final PackageService packageService;

    @PostMapping
    @Operation(summary = "Create package", description = "Create a new package")
    public ResponseEntity<PackageDTO.PackageResponse> create(
            @Valid @RequestBody PackageDTO.CreateRequest request) {
        PackageDTO.PackageResponse response = packageService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all packages", description = "Get all packages")
    public ResponseEntity<List<PackageDTO.PackageResponse>> getAll() {
        return ResponseEntity.ok(packageService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get package", description = "Get package by ID")
    public ResponseEntity<PackageDTO.PackageResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(packageService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update package", description = "Update package information")
    public ResponseEntity<PackageDTO.PackageResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody PackageDTO.UpdateRequest request) {
        return ResponseEntity.ok(packageService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete package", description = "Delete package by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        packageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate")
    @Operation(summary = "Validate package", description = "Validate package by tracking number")
    public ResponseEntity<PackageDTO.PackageResponse> validate(
            @RequestParam String trackingNumber) {
        PackageDTO.PackageResponse response = packageService.getById(
            packageService.validatePackage(trackingNumber).getId()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{trackingNumber}/resend-code")
    @Operation(summary = "Resend retrieval code", description = "Resend retrieval code email to recipient")
    public ResponseEntity<String> resendRetrievalCode(@PathVariable String trackingNumber) {
        packageService.resendRetrievalCode(trackingNumber);
        return ResponseEntity.ok("Retrieval code email sent successfully");
    }
}
