package com.likelion.server.domain.recommendedStartupSupport.web.dto;

import com.likelion.server.domain.recommendedStartupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.mapper.RegionMapper;

public record RecommendedStartupSupportDetailResponse(
        String supportArea,           // 지원 분야
        String title,                 // 제목
        String link,                  // 본문 접속 링크 (K-Startup)
        String startDate,             // YYYY.MM.DD
        String endDate,               // YYYY.MM.DD
        String region,                // 지역
        String businessDuration,      // 업력
        String agency,                // 주관기관
        String targetAge,             // 나이 범위
        String target,                // 지원 대상
        String contact,               // 연락처
        String applyMethod,           // 신청 방법
        String supportDetails,        // 지원 내용(상세)
        String guidanceUrl,           // 안내 페이지 URL
        Boolean isRecruiting,         // 모집 여부
        int suitability,              // 적합도
        String reason                 // 추천 사유
) {
    public static RecommendedStartupSupportDetailResponse of(StartupSupport s, RecommendedStartupSupport r) {
        return new RecommendedStartupSupportDetailResponse(
                s.getSupportArea(),
                s.getTitle(),
                s.getLink(),
                s.formatYmd(s.getStartDate()),
                s.formatYmd(s.getEndDate()),
                RegionMapper.toString(s.getRegion()),
                s.getBusinessDuration(),
                s.getAgency(),
                s.getTargetAge(),
                s.getTarget(),
                s.getContact(),
                s.getApplyMethod(),
                s.getSupportDetails(),
                s.getGuidanceUrl(),
                s.getIsRecruiting(),
                r.getSuitability(),
                r.getReason()
        );
    }
}