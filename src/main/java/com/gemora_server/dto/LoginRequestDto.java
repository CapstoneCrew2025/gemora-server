package com.gemora_server.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String email;
    private String password;
}
