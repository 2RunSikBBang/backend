package com.project.sikbbang.app.domain.admin.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminRole {
    NOT_ACTIVATE_ADMIN("권한이 부여되지 않았습니다."),
    STORE_ADMIN("가게 관리자"),
    SUPER_ADMIN("통합 관리자"),
    BOOTH_OPERATOR("부스 운영자");

    private final String message;
}
