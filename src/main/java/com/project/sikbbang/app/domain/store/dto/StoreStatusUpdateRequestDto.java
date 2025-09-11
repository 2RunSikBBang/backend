package com.project.sikbbang.app.domain.store.dto;

import com.project.sikbbang.app.domain.store.model.StoreStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreStatusUpdateRequestDto {
    private StoreStatus status;
}
