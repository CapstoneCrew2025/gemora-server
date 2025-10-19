package com.gemora_server.controller;

import com.gemora_server.dto.ProfileUpdateDto;
import com.gemora_server.dto.UserProfileDto;
import com.gemora_server.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<UserProfileDto> getUserProfile(
            @RequestHeader("Authorization") String token) {

        UserProfileDto profile = profileService.getUserProfile(token);
        return ResponseEntity.ok(profile);
    }

    @PutMapping
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestBody ProfileUpdateDto request) {

        UserProfileDto updatedProfile = profileService.updateUserProfile(token, request);
        return ResponseEntity.ok(updatedProfile);
    }

}
