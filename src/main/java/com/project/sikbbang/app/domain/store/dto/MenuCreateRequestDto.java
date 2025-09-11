package com.project.sikbbang.app.domain.store.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MenuCreateRequestDto {
    private String name;
    private Integer price;
    private Integer minOrderQuantity;
    private Integer maxOrderQuantity;
    private boolean isRepresentative;
}
