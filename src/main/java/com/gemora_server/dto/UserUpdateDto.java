package com.gemora_server.dto;

import lombok.*;

@Getter
//lombok annotations to generate getters, setters, constructors, and builder pattern methods
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
