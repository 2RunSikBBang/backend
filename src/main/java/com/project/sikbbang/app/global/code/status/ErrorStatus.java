package com.project.sikbbang.app.global.code.status;

import com.project.sikbbang.app.global.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 500
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),

    // 400
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "VALID400", "입력값 유효성 검증 실패"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "PASSWORD400", "비밀번호가 일치하지 않습니다."),
    STORE_NOT_ACCEPTING_ORDERS(HttpStatus.BAD_REQUEST, "STORE400", "해당 가게는 현재 주문을 받지 않습니다."),

    // 401
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED401", "인증정보가 일치하지 않습니다."),

    // 403
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "권한이 없습니다."),
    STORE_NOT_APPROVED(HttpStatus.FORBIDDEN, "STORE403", "상점이 승인되지 않았습니다."),

    // 404
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "리소스를 찾을 수 없습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "리소스를 찾을 수 없습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "STORE404", "리소스를 찾을 수 없습니다."),

    // 409
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "DUPLICATE409", "이미 존재하는 값 입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override public boolean isSuccess() { return false; }
}
