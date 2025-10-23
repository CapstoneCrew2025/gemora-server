package com.gemora_server.service.impl;

import com.gemora_server.dto.UserAdminViewDto;
import com.gemora_server.entity.User;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepo userRepo;

    @Override
    public List<UserAdminViewDto> getAllUsers() {
        return userRepo.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserAdminViewDto getUserById(Long userId) {
        return userRepo.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private UserAdminViewDto toDto(User user) {
        return UserAdminViewDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .contactNumber(user.getContactNumber())
                .role(user.getRole())
                .idFrontImageUrl(user.getIdFrontImageUrl())
                .idBackImageUrl(user.getIdBackImageUrl())
                .selfieImageUrl(user.getSelfieImageUrl())
                .build();
    }
}
