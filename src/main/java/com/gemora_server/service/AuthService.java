package com.gemora_server.service;

import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterRequestDto;

public interface AuthService {

    String registerUser(RegisterRequestDto request);

    LoginResponseDto loginUser(LoginRequestDto request);


}
