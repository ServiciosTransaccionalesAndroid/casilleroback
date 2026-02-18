package com.servientrega.locker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/hash/{password}")
    public String generateHash(@PathVariable String password) {
        return passwordEncoder.encode(password);
    }
    
    @GetMapping("/verify")
    public String verifyHash(@RequestParam String password, @RequestParam String hash) {
        boolean matches = passwordEncoder.matches(password, hash);
        return "Matches: " + matches;
    }
}
