package com.gemora_server.service.impl;

import com.gemora_server.dto.UserAdminViewDto;
import com.gemora_server.dto.UserUpdateDto;
import com.gemora_server.entity.User;
import com.gemora_server.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public UserAdminViewDto updateUser(Long userId, UserUpdateDto updateDto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (updateDto.getName() != null) user.setName(updateDto.getName());
        if (updateDto.getContactNumber() != null) user.setContactNumber(updateDto.getContactNumber());


        userRepo.save(user);
        return toDto(user);
    }

    @Override
    public void deleteUser(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepo.delete(user);
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
