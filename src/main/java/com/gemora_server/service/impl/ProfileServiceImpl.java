package com.gemora_server.service.impl;

import com.gemora_server.dto.ProfileUpdateDto;
import com.gemora_server.dto.UserProfileDto;
import com.gemora_server.entity.User;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.ProfileService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    @Override
    public UserProfileDto getUserProfile(String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.extractUserId(token);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        return mapToDto(user);
    }

    @Override
    public UserProfileDto updateUserProfile(String token, ProfileUpdateDto request) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        Long userId = jwtUtil.extractUserId(token);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));

        if (request.getName() != null && !request.getName().isEmpty()) {
            user.setName(request.getName());
        }
        if (request.getContactNumber() != null && !request.getContactNumber().isEmpty()) {
            user.setContactNumber(request.getContactNumber());
        }
        if (request.getSelfieImageUrl() != null && !request.getSelfieImageUrl().isEmpty()) {
            user.setSelfieImageUrl(request.getSelfieImageUrl());
        }

        userRepo.save(user);
        return mapToDto(user);
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
