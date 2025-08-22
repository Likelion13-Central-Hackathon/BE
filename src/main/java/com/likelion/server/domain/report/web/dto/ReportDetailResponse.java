package com.likelion.server.domain.report.web.dto;

import java.util.List;

public record ReportDetailResponse(
        Long id,
        String title,
        Integer angle,
        String researchMethod,
        String strength,
        String weakness,
        String opportunity,
        String threat,
        List<String> steps,
        String expectedEffect,
        String createdAt,     // yyyy.MM.dd
        List<NewsDto> newsList,
        List<RecommendationDto> recommendations
) {
    public record NewsDto(String title, String link) { }
    public record RecommendationDto(String title, String startDate, String endDate, Integer suitability) { }
}
