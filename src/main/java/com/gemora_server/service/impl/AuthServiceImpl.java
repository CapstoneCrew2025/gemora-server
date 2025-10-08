package com.gemora_server.service.impl;

import com.gemora_server.dto.LoginRequestDto;
import com.gemora_server.dto.LoginResponseDto;
import com.gemora_server.dto.RegisterRequestDto;
import com.gemora_server.entity.User;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.AuthService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public String registerUser(RegisterRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered!");
        }

        User user = User.builder().name(request.getName()).email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).role("USER").build();

        userRepository.save(user);
        return "User registered successfully!";
    }

    @Override
    public LoginResponseDto loginUser(LoginRequestDto request) {
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("Invalid email or password!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password!");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return new LoginResponseDto(token,user.getRole());
    }

}
