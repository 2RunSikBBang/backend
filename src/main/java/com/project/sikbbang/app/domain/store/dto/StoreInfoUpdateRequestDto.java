package com.project.sikbbang.app.domain.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StoreInfoUpdateRequestDto {
    private List<String> productImageUrls;
    private String refundPolicy;
    private String bankAccount;
    private String cancelPhoneNumber;
}
