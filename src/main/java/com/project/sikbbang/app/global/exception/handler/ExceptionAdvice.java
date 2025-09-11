package com.project.sikbbang.app.global.exception.handler;

import com.project.sikbbang.app.global.code.BaseErrorCode;
import com.project.sikbbang.app.global.code.dto.ApiResponse;
import com.project.sikbbang.app.global.code.status.ErrorStatus;
import com.project.sikbbang.app.global.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice(annotations = RestController.class)
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    protected ResponseEntity<ApiResponse<Void>> handleGeneral(GeneralException ex) {
        BaseErrorCode ec = ex.getCode();
        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(ApiResponse.of(ec, null));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleAll(Exception ex, HttpServletRequest req) {
        ex.printStackTrace();
        BaseErrorCode ec = ErrorStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(ec.getHttpStatus())
                .body(ApiResponse.of(ec, null));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            String msg = Optional.ofNullable(fe.getDefaultMessage()).orElse("");
            errors.merge(fe.getField(), msg, (a, b) -> a + ", " + b);
        }

        return ResponseEntity
                .status(ErrorStatus.VALIDATION_FAILED.getHttpStatus())
                .headers(headers)
                .body(ApiResponse.of(ErrorStatus.VALIDATION_FAILED, errors));
    }
}
