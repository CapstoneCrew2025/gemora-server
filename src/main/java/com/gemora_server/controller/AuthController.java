package com.gemora_server.controller;

import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterRequestDto;
import com.gemora_server.dto.RegisterResponseDto;
import com.gemora_server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService userService;

    @PostMapping(value = "/register", consumes = "multipart/form-data")
    public ResponseEntity<?> register(
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("idFrontImage") MultipartFile idFrontImage,
            @RequestParam("idBackImage") MultipartFile idBackImage,
            @RequestParam("selfieImage") MultipartFile selfieImage) {

        try {
            RegisterRequestDto request = RegisterRequestDto.builder()
                    .name(name)
                    .email(email)
                    .password(password)
                    .idFrontImage(idFrontImage)
                    .idBackImage(idBackImage)
                    .selfieImage(selfieImage)
                    .build();

            RegisterResponseDto result = userService.registerUser(request);
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
