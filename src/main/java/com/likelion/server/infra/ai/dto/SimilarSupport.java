package com.likelion.server.infra.ai.dto;

public record SimilarSupport(
        String externalRef,
        double score
) {
}
