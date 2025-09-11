package com.project.sikbbang.app.domain.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StoreLoginRequestDto {
    private Long storeId;
    private String password;
}
