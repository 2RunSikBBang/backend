package com.project.sikbbang.app.domain.admin.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminPasswordChangeRequestDto {
    private Long adminId;
    private String newPassword;
}
