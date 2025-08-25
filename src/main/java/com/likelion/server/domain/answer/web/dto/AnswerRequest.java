package com.likelion.server.domain.answer.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AnswerRequest(
        @Min(1) @Max(5)
        Integer questionNumber,

        @NotBlank(message = "내용을 입력해주세요.")
        String userAnswer
) {}
