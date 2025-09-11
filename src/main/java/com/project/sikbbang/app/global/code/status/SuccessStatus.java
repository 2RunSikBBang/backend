package com.project.sikbbang.app.global.code.status;

import com.project.sikbbang.app.global.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseErrorCode {

    // COMMON code
    OK(HttpStatus.OK, "COMMON200", "요청이 처리되었습니다."),
    CREATED(HttpStatus.CREATED, "COMMON201", "생성 완료"),

    // Custom code
    MENU_CREATED(HttpStatus.CREATED, "MENU201", "메뉴 생성 완료"),
    STORE_CREATED(HttpStatus.CREATED, "STORE201", "가게 생성 완료");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public boolean isSuccess() { return true; }
}
