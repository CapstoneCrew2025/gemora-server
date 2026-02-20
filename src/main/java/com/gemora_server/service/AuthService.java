package com.gemora_server.service;

import com.gemora_server.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    RegisterResponseDto registerUserWithFiles(String name, String email, String password, String contactNumber,
                                              MultipartFile idFrontImage, MultipartFile idBackImage, MultipartFile selfieImage);

    LoginResponseDto loginUser(LoginRequestDto request);

    void sendForgotPasswordOtp(ForgotPasswordRequestDto request);

    void verifyOtpAndResetPassword(VerifyOtpAndResetPasswordDto request);
}
