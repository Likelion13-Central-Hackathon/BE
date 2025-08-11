package com.likelion.server.domain.report.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseResponseCode {
    AUTH_401_INVALID_CREDENTIALS("AUTH_401_INVALID_CREDENTIALS", UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
