package com.likelion.server.infra.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StartupRequest (
        @JsonProperty("idea_title") String ideaTitle,
        @JsonProperty("idea_description") String ideaDescription
){
}
