package com.servientrega.locker.controller;

import com.servientrega.locker.dto.AdminAuthDTO;
import com.servientrega.locker.entity.Admin;
import com.servientrega.locker.repository.AdminRepository;
import com.servientrega.locker.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Admin Authentication", description = "Admin portal authentication endpoints")
public class AdminAuthController {

    private final AdminRepository adminRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    @Operation(summary = "Admin login", description = "Authenticates an admin user and returns JWT token")
    public ResponseEntity<AdminAuthDTO.AuthResponse> login(
            @Valid @RequestBody AdminAuthDTO.LoginRequest request) {
        
        Admin admin = adminRepository.findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!admin.getActive()) {
            throw new RuntimeException("Account is inactive");
        }

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(admin.getEmail());

        AdminAuthDTO.AuthResponse response = new AdminAuthDTO.AuthResponse(
            token,
            admin.getEmail(),
            admin.getName(),
            admin.getRole(),
            "Login successful"
        );

        return ResponseEntity.ok(response);
    }
}
