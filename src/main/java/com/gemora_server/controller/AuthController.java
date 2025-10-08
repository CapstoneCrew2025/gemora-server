package com.gemora_server.controller;

import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterRequestDto;
import com.gemora_server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class  AuthController {

    private final AuthService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDto request) {
        try {
            String result = userService.registerUser(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        try {
            LoginResponseDto result = userService.loginUser(request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new LoginResponseDto(null, "Login failed: " + e.getMessage()));
        }
    }
}
