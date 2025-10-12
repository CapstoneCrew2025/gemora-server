package com.gemora_server.service;

import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterRequestDto;
import com.gemora_server.dto.RegisterResponseDto;

public interface AuthService {

    RegisterResponseDto  registerUser(RegisterRequestDto request);

    LoginResponseDto loginUser(LoginRequestDto request);


}
