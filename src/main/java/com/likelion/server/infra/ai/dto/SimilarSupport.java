package com.likelion.server.infra.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SimilarSupport(
        @JsonProperty("external_ref") String externalRef,
        double score
) {
}
