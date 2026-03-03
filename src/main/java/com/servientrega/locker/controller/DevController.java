package com.servientrega.locker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@Tag(name = "Development", description = "Development utilities - REMOVE IN PRODUCTION")
public class DevController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/hash/{pin}")
    @Operation(summary = "Generate BCrypt hash", description = "Generate BCrypt hash for a PIN - DEV ONLY")
    public ResponseEntity<Map<String, String>> generateHash(@PathVariable String pin) {
        String hash = passwordEncoder.encode(pin);
        
        Map<String, String> response = new HashMap<>();
        response.put("pin", pin);
        response.put("hash", hash);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/hash-all")
    @Operation(summary = "Generate all hashes", description = "Generate hashes for all test PINs")
    public ResponseEntity<Map<String, String>> generateAllHashes() {
        Map<String, String> hashes = new HashMap<>();
        
        hashes.put("1234", passwordEncoder.encode("1234"));
        hashes.put("5678", passwordEncoder.encode("5678"));
        hashes.put("9012", passwordEncoder.encode("9012"));
        hashes.put("123456", passwordEncoder.encode("123456"));
        
        return ResponseEntity.ok(hashes);
    }
}
