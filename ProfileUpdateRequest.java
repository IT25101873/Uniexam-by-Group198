package com.example.uniexam.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequest {
    private String name;
    private String email;
    private String phone;
    private String currentPassword;
    private String newPassword;
    private String confirmNewPassword;
}
