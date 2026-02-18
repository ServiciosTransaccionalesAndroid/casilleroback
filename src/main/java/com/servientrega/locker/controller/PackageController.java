package com.servientrega.locker.controller;

import com.servientrega.locker.dto.PackageValidationResponse;
import com.servientrega.locker.entity.Package;
import com.servientrega.locker.service.PackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
@Tag(name = "Packages", description = "Package validation endpoints")
public class PackageController {

    private final PackageService packageService;

    @GetMapping("/validate")
    @Operation(summary = "Validate package", description = "Validates a package by tracking number against ERP")
    public ResponseEntity<PackageValidationResponse> validatePackage(
            @RequestParam String trackingNumber) {
        
        Package pkg = packageService.validatePackage(trackingNumber);
        
        if (pkg == null) {
            return ResponseEntity.notFound().build();
        }

        PackageValidationResponse response = new PackageValidationResponse(
            pkg.getTrackingNumber(),
            pkg.getRecipientName(),
            pkg.getRecipientPhone(),
            pkg.getRecipientEmail(),
            new PackageValidationResponse.PackageDimensions(
                pkg.getWidth(),
                pkg.getHeight(),
                pkg.getDepth(),
                pkg.getWeight()
            ),
            pkg.getStatus().name()
        );

        return ResponseEntity.ok(response);
    }
}
