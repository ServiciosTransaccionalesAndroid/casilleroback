package com.servientrega.locker.controller;

import com.servientrega.locker.dto.RetrievalDTO;
import com.servientrega.locker.entity.RetrievalCode;
import com.servientrega.locker.service.RetrievalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/retrievals")
@RequiredArgsConstructor
@Tag(name = "Retrievals", description = "Package retrieval endpoints")
public class RetrievalController {

    private final RetrievalService retrievalService;

    @GetMapping("/validate")
    @Operation(summary = "Validate retrieval code", description = "Validates a retrieval code")
    public ResponseEntity<RetrievalDTO.RetrievalValidationResponse> validateCode(
            @RequestParam String code) {
        
        RetrievalCode retrievalCode = retrievalService.validateRetrievalCode(code);
        
        if (retrievalCode == null) {
            RetrievalDTO.RetrievalValidationResponse response = new RetrievalDTO.RetrievalValidationResponse(
                false, null, null, null, "Invalid, expired, or already used code"
            );
            return ResponseEntity.ok(response);
        }

        RetrievalDTO.RetrievalValidationResponse response = new RetrievalDTO.RetrievalValidationResponse(
            true,
            retrievalCode.getDeposit().getCompartment().getId(),
            retrievalCode.getDeposit().getPackageEntity().getTrackingNumber(),
            retrievalCode.getExpiresAt(),
            "Code is valid"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Process retrieval", description = "Processes a package retrieval")
    public ResponseEntity<RetrievalDTO.RetrievalResponse> processRetrieval(
            @Valid @RequestBody RetrievalDTO.RetrievalRequest request) {
        
        RetrievalService.RetrievalResult result = retrievalService.processRetrieval(
            request.code(),
            request.secretPin(),
            request.photoUrl()
        );

        RetrievalDTO.RetrievalResponse response = new RetrievalDTO.RetrievalResponse(
            result.retrievalId(),
            result.timestamp(),
            "Package retrieved successfully"
        );

        return ResponseEntity.ok(response);
    }
}
