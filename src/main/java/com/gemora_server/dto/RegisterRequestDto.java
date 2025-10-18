package com.gemora_server.dto;


import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Builder
@Data
public class RegisterRequestDto {
    private String name;
    private String email;
    private String contactNumber;
    private String password;

    private MultipartFile idFrontImage;
    private MultipartFile idBackImage;
    private MultipartFile selfieImage;

}
