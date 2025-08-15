package com.likelion.server.domain.startupSupport.exception;

import com.likelion.server.global.response.code.BaseResponseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.likelion.server.global.constant.StaticValue.BAD_REQUEST;
import static com.likelion.server.global.constant.StaticValue.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum StartupSupportErrorCode implements BaseResponseCode {
    REGION_400_REQUIRED("REGION_400_REQUIRED", BAD_REQUEST, "region 파라미터는 필수입니다."),
    REGION_400_INVALID_VALUE("REGION_400_INVALID_VALUE", BAD_REQUEST, "지원하지 않는 region 값입니다."),
    STARTUP_SUPPORT_404_NOT_FOUND("STARTUP_SUPPORT_404_NOT_FOUND", NOT_FOUND, "해당 Id의 창업 지원사업을 찾을 수 없습니다.");


    private final String code;
    private final int httpStatus;
    private final String message;
}
