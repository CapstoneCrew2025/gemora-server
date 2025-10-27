package com.gemora_server.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CertificateDto {

    private Long id;
    private String certificateNumber;
    private String issuingAuthority;
    private LocalDate issueDate;
    private String fileUrl;
    private Boolean verified;
    private LocalDateTime uploadedAt;
    private LocalDateTime verifiedAt;


}
