package com.likelion.server.domain.admin.web.dto;

public record StartupSupportSyncRequest(
        @JsonProperty("after_external_ref")
        String afterExternalRef,
        @JsonProperty("expired_external_refs")
        List<String> expiredExternalRefs
) {}

