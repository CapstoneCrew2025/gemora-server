package com.gemora_server.controller;

import com.gemora_server.dto.*;
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
            @RequestPart("contactNumber") String contactNumber,
            @RequestPart(value = "idFrontImage", required = false) MultipartFile idFrontImage,
            @RequestPart(value = "idBackImage", required = false) MultipartFile idBackImage,
            @RequestPart(value = "selfieImage", required = false) MultipartFile selfieImage
    ) {
        try {
            RegisterResponseDto result = userService.registerUserWithFiles(
                    name, email, password,contactNumber, idFrontImage, idBackImage, selfieImage);
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

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequestDto request) {

        try {
            userService.sendForgotPasswordOtp(request);
            return ResponseEntity.ok(
                    new CommonResponseDto("OTP sent to registered email")
            );
        } catch (RuntimeException ex) {
            return ResponseEntity
                    .status(404)
                    .body(new CommonResponseDto(ex.getMessage()));
        }
    }


    @PostMapping("/reset-password")
    public ResponseEntity<CommonResponseDto> resetPassword(
            @RequestBody VerifyOtpAndResetPasswordDto request) {

        userService.verifyOtpAndResetPassword(request);
        return ResponseEntity.ok(
                new CommonResponseDto("Password reset successful")
        );
    }

}
