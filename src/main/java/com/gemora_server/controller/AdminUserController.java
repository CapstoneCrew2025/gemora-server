package com.gemora_server.controller;

import com.gemora_server.dto.UserAdminViewDto;
import com.gemora_server.dto.UserUpdateDto;
import com.gemora_server.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public ResponseEntity<List<UserAdminViewDto>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserAdminViewDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserAdminViewDto> updateUser(
            @PathVariable Long userId,
            @RequestBody UserUpdateDto updateDto) {
        return ResponseEntity.ok(adminUserService.updateUser(userId, updateDto));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

}

