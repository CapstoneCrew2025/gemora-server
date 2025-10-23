package com.gemora_server.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAdminViewDto {
    private Long id;
    private String name;
    private String email;
    private String contactNumber;

    private String idFrontImageUrl;
    private String idBackImageUrl;
    private String selfieImageUrl;
}
