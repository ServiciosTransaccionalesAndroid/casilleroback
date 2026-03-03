package com.servientrega.locker.controller;

import com.servientrega.locker.dto.ActivityDTO;
import com.servientrega.locker.service.ActivityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
@Tag(name = "Activity", description = "Deposits and retrievals activity endpoints")
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/deposits")
    @Operation(summary = "List all deposits", description = "Returns all deposits ordered by date (newest first)")
    public ResponseEntity<List<ActivityDTO.DepositDetail>> getAllDeposits() {
        return ResponseEntity.ok(activityService.getAllDeposits());
    }

    @GetMapping("/deposits/{id}")
    @Operation(summary = "Get deposit details", description = "Returns deposit details by ID")
    public ResponseEntity<ActivityDTO.DepositDetail> getDepositById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getDepositById(id));
    }

    @GetMapping("/retrievals")
    @Operation(summary = "List all retrievals", description = "Returns all retrievals ordered by date (newest first)")
    public ResponseEntity<List<ActivityDTO.RetrievalDetail>> getAllRetrievals() {
        return ResponseEntity.ok(activityService.getAllRetrievals());
    }

    @GetMapping("/retrievals/{id}")
    @Operation(summary = "Get retrieval details", description = "Returns retrieval details by ID")
    public ResponseEntity<ActivityDTO.RetrievalDetail> getRetrievalById(@PathVariable Long id) {
        return ResponseEntity.ok(activityService.getRetrievalById(id));
    }
}
