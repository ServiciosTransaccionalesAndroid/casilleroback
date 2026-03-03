package com.servientrega.locker.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/util")
@RequiredArgsConstructor
public class UtilController {

    private final PasswordEncoder passwordEncoder;

    @GetMapping("/hash")
    public String generateHash(@RequestParam String password) {
        return passwordEncoder.encode(password);
    }
}
