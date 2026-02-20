package com.gemora_server.service.impl;

import com.gemora_server.dto.*;
import com.gemora_server.entity.PasswordResetOtp;
import com.gemora_server.entity.User;
import com.gemora_server.repo.PasswordResetOtpRepo;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.AuthService;
import com.gemora_server.service.EmailService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final PasswordResetOtpRepo otpRepo;
    private final EmailService emailService;

    private static final String UPLOAD_SUBDIR = "uploads" + File.separator + "users" + File.separator;

    @Override
    public RegisterResponseDto registerUserWithFiles(String name, String email, String password, String contactNumber,
                                                     MultipartFile idFrontImage, MultipartFile idBackImage, MultipartFile selfieImage) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered!");
        }

        String idFrontUrl = saveFile(idFrontImage, "id_front");
        String idBackUrl = saveFile(idBackImage, "id_back");
        String selfieUrl = saveFile(selfieImage, "selfie");

        User user = User.builder()
                .name(name)
                .email(email)
                .password(passwordEncoder.encode(password))
                .contactNumber(contactNumber)
                .idFrontImageUrl(idFrontUrl)
                .idBackImageUrl(idBackUrl)
                .selfieImageUrl(selfieUrl)
                .role("USER")
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new RegisterResponseDto("User registered successfully!", token, user.getRole());
    }

    @Override
    public LoginResponseDto loginUser(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password!");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        return new LoginResponseDto(token, user.getRole());
    }

    private String saveFile(MultipartFile file, String prefix) {
        if (file == null || file.isEmpty()) return null;
        try {
            String basePath = System.getProperty("user.dir") + File.separator + UPLOAD_SUBDIR;
            File uploadDir = new File(basePath);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                throw new IOException("Failed to create upload directory: " + basePath);
            }

            String original = file.getOriginalFilename();
            String sanitized = (original == null ? "file" : original).replaceAll("[^a-zA-Z0-9._-]", "_");
            String fileName = prefix + "_" + System.currentTimeMillis() + "_" + sanitized;
            File destinationFile = new File(uploadDir, fileName);
            file.transferTo(destinationFile);
            String relativePath = "uploads/users/" + fileName;
            return "http://192.168.8.101:8080/" + relativePath.replace("\\", "/");
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }


    @Override
    public void sendForgotPasswordOtp(ForgotPasswordRequestDto request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        PasswordResetOtp entity = PasswordResetOtp.builder()
                .email(user.getEmail())
                .otp(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepo.save(entity);
        emailService.sendOtpEmail(user.getEmail(), otp);
    }


    @Override
    public void verifyOtpAndResetPassword(VerifyOtpAndResetPasswordDto request) {

        PasswordResetOtp otpEntity = otpRepo
                .findTopByEmailOrderByExpiresAtDesc(request.getEmail())
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otpEntity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }

        if (!otpEntity.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        otpRepo.delete(otpEntity);
    }

}

