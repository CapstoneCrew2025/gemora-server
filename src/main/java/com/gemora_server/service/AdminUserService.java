package com.gemora_server.service;

import com.gemora_server.dto.UserAdminViewDto;
import com.gemora_server.dto.UserUpdateDto;

import java.util.List;

public interface AdminUserService {
    List<UserAdminViewDto> getAllUsers();
    UserAdminViewDto getUserById(Long userId);
    UserAdminViewDto updateUser(Long userId, UserUpdateDto updateDto);
    void deleteUser(Long userId);
}
