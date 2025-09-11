package com.project.sikbbang.app.domain.admin.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SuperAdminLoginRequestDto {
    private String username;
    private String password;
}
