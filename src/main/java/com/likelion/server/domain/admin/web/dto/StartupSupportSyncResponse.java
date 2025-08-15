package com.likelion.server.domain.admin.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StartupSupportSyncResponse(
        String title,
        @JsonProperty("support_area") String supportArea,
        String region,
        @JsonProperty("business_duration") String businessDuration,
        String agency,
        @JsonProperty("target_age") String targetAge,
        String target,
        String contact,
        String link,
        @JsonProperty("start_date") String startDate, // yyyy-MM-dd
        @JsonProperty("end_date") String endDate, // yyyy-MM-dd
        @JsonProperty("apply_method") String applyMethod,
        @JsonProperty("support_details") String supportDetails,
        @JsonProperty("external_ref") String externalRef,
        @JsonProperty("guidance_url") String guidanceUrl,
        @JsonProperty("is_recruiting") Boolean isRecruiting
) {
}
