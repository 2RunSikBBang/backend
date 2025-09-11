package com.project.sikbbang.app.domain.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItemDto {
    private Long menuId;
    private String menuName;
    private Integer quantity;
    private Integer price;
}
