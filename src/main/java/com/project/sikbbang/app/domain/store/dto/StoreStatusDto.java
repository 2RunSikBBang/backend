package com.project.sikbbang.app.domain.store.dto;

import com.project.sikbbang.app.domain.store.model.StoreStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StoreStatusDto {
    private Long storeId;
    private StoreStatus status;
    private String message;
}
