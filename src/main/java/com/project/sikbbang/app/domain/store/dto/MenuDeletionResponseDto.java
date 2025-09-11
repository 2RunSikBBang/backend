package com.project.sikbbang.app.domain.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuDeletionResponseDto {
    private String message;
    private Long menuId;
}
