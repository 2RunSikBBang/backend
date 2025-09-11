package com.project.sikbbang.app.domain.store.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StoreStatus {
    OPEN("현재 주문이 가능해요."),
    DELAYED("현재 주문량이 많아 주문이 지연되고 있어요."),
    UNAVAILABLE("현재 주문폭주로 주문이 불가능해요."),
    NOT_READY("매진되었거나 아직 오픈 전이에요.");

    private final String message;
}
