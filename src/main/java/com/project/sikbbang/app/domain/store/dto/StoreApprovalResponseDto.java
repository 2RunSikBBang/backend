package com.project.sikbbang.app.domain.store.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreApprovalResponseDto {
    private String status;
    private Long storeId;
}
