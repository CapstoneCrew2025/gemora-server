package com.gemora_server.service;

import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterRequestDto;
import com.gemora_server.dto.RegisterResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

//    RegisterResponseDto  registerUser(RegisterRequestDto request);


    RegisterResponseDto registerUserWithFiles(String name, String email, String password, String contactNumber,
                                              MultipartFile idFrontImage, MultipartFile idBackImage, MultipartFile selfieImage);

    LoginResponseDto loginUser(LoginRequestDto request);


}
