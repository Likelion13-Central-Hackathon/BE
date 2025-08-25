package com.likelion.server.domain.admin.web.dto;
import java.util.List;

public record StartupSupportSyncRequest(
        String afterExternalRef,
        List<String> expiredExternalRefs
) {}

