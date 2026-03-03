package com.servientrega.locker.controller;

import com.servientrega.locker.dto.AuthDTO;
import com.servientrega.locker.entity.Courier;
import com.servientrega.locker.repository.CourierRepository;
import com.servientrega.locker.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final CourierRepository courierRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/courier/login")
    @Operation(summary = "Courier login", description = "Authenticates a courier and returns JWT token")
    public ResponseEntity<AuthDTO.AuthResponse> login(
            @Valid @RequestBody AuthDTO.LoginRequest request) {
        
        Courier courier = courierRepository.findByEmployeeId(request.employeeId())
            .orElseThrow(() -> new RuntimeException("Courier not found"));

        if (!courier.getActive()) {
            throw new RuntimeException("Courier is inactive");
        }

        if (!passwordEncoder.matches(request.pin(), courier.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        String token = jwtService.generateToken(courier.getEmployeeId());

        AuthDTO.AuthResponse response = new AuthDTO.AuthResponse(
            token,
            courier.getEmployeeId(),
            courier.getName(),
            "Login successful"
        );

        return ResponseEntity.ok(response);
    }
}
