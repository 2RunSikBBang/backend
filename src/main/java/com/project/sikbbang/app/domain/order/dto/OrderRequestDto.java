package com.project.sikbbang.app.domain.order.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderRequestDto {
    private String phoneNumber;
    private String customerName;
    private String deliveryAddress;
    private List<OrderItemDto> orderItems;
}
