package com.likelion.server.domain.user.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseResponseCode {
    USER_404_NOT_FOUND_BY_IDEA("USER_404_NOT_FOUND_BY_IDEA", NOT_FOUND, "해당 아이디어의 회원을 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
