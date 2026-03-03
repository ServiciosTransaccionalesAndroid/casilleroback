package com.servientrega.locker.controller;

import com.servientrega.locker.dto.RecipientDTO;
import com.servientrega.locker.service.RecipientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipients")
@RequiredArgsConstructor
@Tag(name = "Recipients", description = "Client/Recipient management endpoints")
public class RecipientController {

    private final RecipientService recipientService;

    @PostMapping
    @Operation(summary = "Create recipient", description = "Create a new recipient/client")
    public ResponseEntity<RecipientDTO.RecipientResponse> create(
            @Valid @RequestBody RecipientDTO.CreateRequest request) {
        RecipientDTO.RecipientResponse response = recipientService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all recipients", description = "Get all recipients")
    public ResponseEntity<List<RecipientDTO.RecipientResponse>> getAll() {
        return ResponseEntity.ok(recipientService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipient", description = "Get recipient by ID")
    public ResponseEntity<RecipientDTO.RecipientResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(recipientService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update recipient", description = "Update recipient information")
    public ResponseEntity<RecipientDTO.RecipientResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody RecipientDTO.UpdateRequest request) {
        return ResponseEntity.ok(recipientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete recipient", description = "Delete recipient by ID")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
