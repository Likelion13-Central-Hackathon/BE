package com.likelion.server.global.scheduler.dto;

public record WeeklyTargetDto(
        Long ideaId,
        String email,
        Long userId) {
}