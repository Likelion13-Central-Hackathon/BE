package com.likelion.server.domain.answer.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum BusinessPlanQuestion {
    ITEM_OVERVIEW(1, "창업 아이템 개요"),
    PROBLEM_AWARENESS(2, "문제 인식"),
    FEASIBILITY(3, "실현 가능성"),
    GROWTH_STRATEGY(4, "성장 전략"),
    TEAM(5, "팀 구성");

    private final int number;
    private final String title;

    public static BusinessPlanQuestion fromNumber(int number) {
        return Arrays.stream(values())
                .filter(q -> q.number == number)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("문항 번호는 1~5만 허용됩니다."));
    }
}