package com.likelion.server.domain.report.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ReportErrorCode implements BaseResponseCode {
    REPORT_404_NOT_FOUND("REPORT_404_NOT_FOUND", NOT_FOUND, "해당 회원의 레포트를 찾을 수 없습니다.");

    private final String code;
    private final int httpStatus;
    private final String message;
}
