package com.project.sikbbang.app.domain.store.dto;

import com.project.sikbbang.app.domain.store.model.Store;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StoreInfoDto {
    private Long id;
    private String name;
    private String profileImageUrl;
    private List<String> productImageUrls;
    private String refundPolicy;
    private String bankAccount;
    private String cancelPhoneNumber;
    private String statusMessage;

    public static StoreInfoDto fromEntity(Store store) {
        return StoreInfoDto.builder()
                .id(store.getId())
                .name(store.getName())
                .profileImageUrl(store.getProfileImageUrl())
                .productImageUrls(store.getProductImageUrls())
                .refundPolicy(store.getRefundPolicy())
                .bankAccount(store.getBankAccount())
                .cancelPhoneNumber(store.getCancelPhoneNumber())
                .statusMessage(store.getStatus().getMessage())
                .build();
    }
}
