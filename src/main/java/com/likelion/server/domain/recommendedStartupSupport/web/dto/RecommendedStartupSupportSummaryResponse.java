package com.likelion.server.domain.recommendedStartupSupport.web.dto;

public record RecommendedStartupSupportSummaryResponse(
        Long recommendedId,
        int suitability,
        String supportArea,
        String title,
        String agency,
        String startDate,
        String endDate
) {
}
