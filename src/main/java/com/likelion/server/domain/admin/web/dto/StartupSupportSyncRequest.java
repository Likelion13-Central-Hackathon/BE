package com.likelion.server.domain.admin.web.dto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StartupSupportSyncRequest(
        @JsonProperty("after_external_ref")
        String afterExternalRef,
        @JsonProperty("expired_external_refs")
        List<String> expiredExternalRefs
) {}

