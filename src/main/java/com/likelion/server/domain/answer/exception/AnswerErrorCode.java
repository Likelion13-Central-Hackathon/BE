package com.likelion.server.domain.answer.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.UNAUTHORIZED;

@Getter
@AllArgsConstructor
public enum AnswerErrorCode implements BaseResponseCode {
    ANSWERS_404_NOT_FOUND("ANSWERS_404_NOT_FOUND", UNAUTHORIZED, "해당 ID에 해당하는 답변을 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
