package com.project.sikbbang.app.domain.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminActivationResponseDto {
    private String status;
    private Long adminId;
}
