package com.servientrega.locker.controller;

import com.servientrega.locker.dto.DepositDTO;
import com.servientrega.locker.service.DepositService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
@Tag(name = "Deposits", description = "Package deposit endpoints")
public class DepositController {

    private final DepositService depositService;

    @PostMapping
    @Operation(summary = "Register deposit", description = "Registers a package deposit and generates retrieval code")
    public ResponseEntity<DepositDTO.DepositResponse> createDeposit(
            @Valid @RequestBody DepositDTO.DepositRequest request) {
        
        DepositService.DepositResult result = depositService.processDeposit(
            request.trackingNumber(),
            request.lockerId(),
            request.courierEmployeeId(),
            request.photoUrl()
        );

        DepositDTO.DepositResponse response = new DepositDTO.DepositResponse(
            result.depositId(),
            result.compartmentNumber(),
            result.retrievalCode(),
            result.secretPin(),
            result.expiresAt(),
            "Deposit registered successfully. Use compartment #" + result.compartmentNumber() + ". Retrieval code: " + result.retrievalCode()
        );

        return ResponseEntity.ok(response);
    }
}
