package com.likelion.server.domain.report.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// 최신 리포트 조회 Request Dto
public record LatestReportDetailRequest(
        @Email
        @NotBlank(message = "이메일은 필수 입력 값 입니다.")
        String email,

        @NotBlank(message = "비밀번호는 필수 입력 값 입니다.")
        String password
) { }