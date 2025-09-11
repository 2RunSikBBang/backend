package com.project.sikbbang.app.domain.menu.dto;

import com.project.sikbbang.app.domain.menu.model.Menu;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuDto {
    private Long id;
    private String name;
    private Integer price;
    private Integer minOrderQuantity;
    private Integer maxOrderQuantity;
    private boolean isRepresentative;

    public static MenuDto fromEntity(Menu menu) {
        return MenuDto.builder()
                .id(menu.getId())
                .name(menu.getName())
                .price(menu.getPrice())
                .minOrderQuantity(menu.getMinOrderQuantity())
                .maxOrderQuantity(menu.getMaxOrderQuantity())
                .isRepresentative(menu.isRepresentative())
                .build();
    }
}
