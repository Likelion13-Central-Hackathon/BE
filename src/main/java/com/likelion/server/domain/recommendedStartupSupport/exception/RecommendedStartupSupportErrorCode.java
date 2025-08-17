package com.likelion.server.domain.recommendedStartupSupport.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.INTERNAL_SERVER_ERROR;
import static com.likelion.server.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum RecommendedStartupSupportErrorCode implements BaseResponseCode {
    RECOMMENDED_STARTUP_SUPPORT_404_NOT_FOUND(
            "RECOMMENDED_STARTUP_SUPPORT_404_NOT_FOUND", NOT_FOUND, "해당 Id의 추천 창업 지원사업을 찾을 수 없습니다."),
    RECOMMENDED_STARTUP_SUPPORT_404_EMPTY_FOR_REPORT(
            "RECOMMENDED_STARTUP_SUPPORT_404_EMPTY_FOR_REPORT", NOT_FOUND, "해당 레포트에 생성된 추천 창업 지원사업이 없습니다."),
    RECOMMENDED_STARTUP_SUPPORT_500_CREATE_FAILED(
            "RECOMMENDED_STARTUP_SUPPORT_500_CREATE_FAILED",
            INTERNAL_SERVER_ERROR,
            "추천 창업 지원사업 생성 중 오류가 발생했습니다."
    );

    private final String code;
    private final int httpStatus;
    private final String message;
}
