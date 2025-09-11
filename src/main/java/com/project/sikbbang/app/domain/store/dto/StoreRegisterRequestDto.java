package com.project.sikbbang.app.domain.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreRegisterRequestDto {
    private String storeName;
    private String profileImageUrl;
    private String password;
}
