package com.project.sikbbang.app.domain.order.dto;

import com.project.sikbbang.app.domain.order.model.Order;
import com.project.sikbbang.app.domain.order.model.OrderStatus;
import com.project.sikbbang.app.domain.store.dto.StoreInfoDto;
import lombok.Builder;
import lombok.Getter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrderDto {
    private Long orderId;
    private String customerName;
    private String phoneNumber;
    private String deliveryAddress;
    private OrderStatus status;
    private Integer totalPrice;
    private String orderDate;
    private List<OrderItemDto> orderItems;
    private StoreInfoDto storeInfo;

    public static OrderDto fromEntity(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        return OrderDto.builder()
                .orderId(order.getId())
                .customerName(order.getCustomerName())
                .phoneNumber(order.getPhoneNumber())
                .deliveryAddress(order.getDeliveryAddress())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .orderDate(order.getOrderDate().format(formatter))
                .orderItems(order.getOrderItems().stream()
                        .map(orderItem -> OrderItemDto.builder()
                                .menuId(orderItem.getMenu().getId())
                                .menuName(orderItem.getMenu().getName())
                                .quantity(orderItem.getQuantity())
                                .price(orderItem.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .storeInfo(StoreInfoDto.fromEntity(order.getStore()))
                .build();
    }
}
