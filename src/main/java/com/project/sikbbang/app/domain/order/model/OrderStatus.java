package com.project.sikbbang.app.domain.order.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    PENDING("주문을 확인하고 있습니다."),
    PREPARING("조리중입니다..."),
    DELIVERING("배송 중 이에요...."),
    COMPLETED("배송 완료"),
    CANCELED("미입금으로 취소 되었어요");

    private final String message;
}
