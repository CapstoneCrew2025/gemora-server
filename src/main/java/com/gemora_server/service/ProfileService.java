package com.gemora_server.service;

import com.gemora_server.dto.ProfileUpdateDto;
import com.gemora_server.dto.UserProfileDto;

public interface ProfileService {

    UserProfileDto getUserProfile(String token);
    UserProfileDto updateUserProfile(String token, ProfileUpdateDto request);
}
