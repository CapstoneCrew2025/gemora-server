package com.gemora_server.service.impl;

import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterRequestDto;
import com.gemora_server.dto.RegisterResponseDto;
import com.gemora_server.entity.User;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.AuthService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
            return UPLOAD_SUBDIR + fileName;
        } catch (IOException e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}

