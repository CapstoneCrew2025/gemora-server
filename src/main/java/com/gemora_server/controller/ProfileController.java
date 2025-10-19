package com.gemora_server.controller;

import com.gemora_server.dto.ProfileUpdateDto;
import com.gemora_server.dto.UserProfileDto;
import com.gemora_server.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PutMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<UserProfileDto> updateUserProfile(
            @RequestHeader("Authorization") String token,
            @RequestPart(value = "name", required = false) String name,
            @RequestPart(value = "contactNumber", required = false) String contactNumber,
            @RequestPart(value = "selfieImage", required = false) MultipartFile selfieImage) {

        ProfileUpdateDto request = new ProfileUpdateDto(name, contactNumber, selfieImage);
        UserProfileDto updatedProfile = profileService.updateUserProfile(token, request);
        return ResponseEntity.ok(updatedProfile);
    }

}
