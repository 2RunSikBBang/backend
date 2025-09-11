package com.project.sikbbang.app.domain.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminLoginRequestDto {
    private String username;
    private String password;
}
