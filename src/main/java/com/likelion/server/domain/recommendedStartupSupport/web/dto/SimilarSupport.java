package com.likelion.server.domain.recommendedStartupSupport.web.dto;

public record SimilarSupport(
        String external_ref,
        double score
) {
}
