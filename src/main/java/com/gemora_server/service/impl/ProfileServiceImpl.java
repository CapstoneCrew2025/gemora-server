package com.gemora_server.service.impl;

import com.gemora_server.dto.ProfileUpdateDto;
import com.gemora_server.dto.UserProfileDto;
import com.gemora_server.entity.User;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.ProfileService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    private static final String UPLOAD_SUBDIR = "uploads" + File.separator + "users" + File.separator;

    @Override
    public UserProfileDto getUserProfile(String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token!");
        }


        Long userId = jwtUtil.extractUserId(token);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return mapToDto(user);
    }

    @Override
    public UserProfileDto updateUserProfile(String token, ProfileUpdateDto request) {
        if (token.startsWith("Bearer ")) token = token.substring(7);

        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or expired token!");
        }

        Long userId = jwtUtil.extractUserId(token);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        // Update name/contact
        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getContactNumber() != null && !request.getContactNumber().isEmpty()) {
            user.setContactNumber(request.getContactNumber());
        }

        // Update selfie if provided
        MultipartFile selfie = request.getSelfieImage();
        if (selfie != null && !selfie.isEmpty()) {

            // Delete the old selfie if it exists
            deleteOldFile(user.getSelfieImageUrl());

            // Save the new selfie
            String selfieUrl = saveFile(selfie, "selfie");
            user.setSelfieImageUrl(selfieUrl);
        }

        userRepo.save(user);
        return mapToDto(user);
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

    private void deleteOldFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        try {
            // Convert the URL to local file path
            URL url = new URL(fileUrl);
            String filePath = url.getPath(); // /uploads/users/selfie_12345.jpg
            File oldFile = new File(System.getProperty("user.dir"), filePath);

            if (oldFile.exists()) {
                if (oldFile.delete()) {
                    System.out.println("✅ Deleted old selfie: " + oldFile.getAbsolutePath());
                } else {
                    System.err.println("⚠️ Failed to delete old selfie: " + oldFile.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error deleting old selfie: " + e.getMessage());
        }
    }

    private UserProfileDto mapToDto(User user) {
        return UserProfileDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .selfieImageUrl(user.getSelfieImageUrl())
                .role(user.getRole())
                .build();
    }

}
