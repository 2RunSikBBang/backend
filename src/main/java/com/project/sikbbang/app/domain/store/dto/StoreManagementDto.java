package com.project.sikbbang.app.domain.store.dto;

import com.project.sikbbang.app.domain.store.model.StoreInspection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreManagementDto {
    private Long storeId;
    private String storeName;
    private StoreInspection inspection;
    private Long adminId;
    private String adminUsername;
}
