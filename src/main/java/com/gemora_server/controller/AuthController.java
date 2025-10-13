package com.gemora_server.controller;

import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterResponseDto;
import com.gemora_server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {

    private final AuthService userService;


    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerWithFiles(
            @RequestPart("name") String name,
            @RequestPart("email") String email,
            @RequestPart("password") String password,
            @RequestPart(value = "idFrontImage", required = false) MultipartFile idFrontImage,
            @RequestPart(value = "idBackImage", required = false) MultipartFile idBackImage,
            @RequestPart(value = "selfieImage", required = false) MultipartFile selfieImage
    ) {
        try {
            RegisterResponseDto result = userService.registerUserWithFiles(
                    name, email, password, idFrontImage, idBackImage, selfieImage);
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
            return ResponseEntity.badRequest().body(
                    new LoginResponseDto(null, "Login failed: " + e.getMessage())
            );
        }
    }
}
