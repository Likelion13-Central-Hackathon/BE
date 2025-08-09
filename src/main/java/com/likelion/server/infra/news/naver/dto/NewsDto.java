package com.likelion.server.infra.news.naver.dto;

public record NewsDto(
        String title,
        String link,
        String pubDate,
        String source
) {}
