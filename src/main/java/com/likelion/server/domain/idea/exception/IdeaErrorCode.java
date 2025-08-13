package com.likelion.server.domain.idea.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.NOT_FOUND;
import static com.likelion.server.global.constant.StaticValue.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum IdeaErrorCode implements BaseResponseCode {
    IDEA_404_NOT_FOUND("IDEA_404_NOT_FOUND", NOT_FOUND, "해당 ID의 창업 아이디어를 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
