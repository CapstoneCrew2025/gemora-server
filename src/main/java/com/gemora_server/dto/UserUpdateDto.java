package com.gemora_server.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDto {
    private String name;
    private String email;
    private String contactNumber;
    private String role;
}
