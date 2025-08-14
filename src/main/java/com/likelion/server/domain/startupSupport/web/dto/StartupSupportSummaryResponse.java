package com.likelion.server.domain.startupSupport.web.dto;

public record StartupSupportSummaryResponse(
        Long id,
        String supportArea,
        String region,
        String title,
        String link
) {
}