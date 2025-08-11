package com.likelion.server.domain.idea.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum IdeaErrorCode implements BaseResponseCode {

    IDEA_400_INVALID_ENUM("IDEA_400_INVALID_ENUM", BAD_REQUEST, "잘못된 Enum 값입니다."),
    IDEA_400_MISSING_REQUIRED("IDEA_400_MISSING_REQUIRED", BAD_REQUEST, "필수 입력 값이 누락되었습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}