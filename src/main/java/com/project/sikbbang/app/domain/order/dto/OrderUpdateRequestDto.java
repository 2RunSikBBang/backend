package com.project.sikbbang.app.domain.order.dto;

import com.project.sikbbang.app.domain.order.model.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderUpdateRequestDto {
    private OrderStatus status;
}
