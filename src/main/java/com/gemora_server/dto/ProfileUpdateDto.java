package com.gemora_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateDto {
    private String name;
    private String contactNumber;
    private MultipartFile selfieImage;

}
