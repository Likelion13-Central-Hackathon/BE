package com.likelion.server.domain.recommendedStartupSupport.web.dto;

import com.likelion.server.domain.recommendedStartupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.mapper.RegionMapper;

public record RecommendedStartupSupportDetailResponse(
        int suitability,              // 적합도
        String supportArea,           // 지원 분야
        String agency,                // 주관기관
        String title,                 // 제목
        String link,                  // 본문 접속 링크 (K-Startup)
        String startDate,             // 시작일 (mm.dd)
        String endDate,               // 종료일 (mm.dd)
        String region,                // 지역
        String businessDuration,      // 업력
        String targetAge,             // 나이 범위
        String target,                // 지원 대상
        String contact,               // 연락처
        String applyMethod,           // 신청 방법
        String supportDetails,        // 지원 내용(상세)
        String guidanceUrl,           // 안내 페이지 URL
        Boolean isRecruiting,         // 모집 여부
        String reason                 // 추천 사유
) {
    public static RecommendedStartupSupportDetailResponse of(StartupSupport s, RecommendedStartupSupport r) {
        return new RecommendedStartupSupportDetailResponse(
                r.getSuitability(),              // 적합도
                s.getSupportArea(),              // 지원 분야
                s.getAgency(),                   // 주관기관
                s.getTitle(),                    // 제목
                s.getLink(),                     // 본문 접속 링크
                s.formatMd(s.getStartDate()),    // 시작일
                s.formatMd(s.getEndDate()),      // 종료일
                RegionMapper.toString(s.getRegion()), // 지역
                s.getBusinessDuration(),         // 업력
                s.getTargetAge(),                // 나이 범위
                s.getTarget(),                   // 지원 대상
                s.getContact(),                  // 연락처
                s.getApplyMethod(),              // 신청 방법
                s.getSupportDetails(),           // 지원 내용
                s.getGuidanceUrl(),              // 안내 페이지
                s.getIsRecruiting(),             // 모집 여부
                r.getReason()                    // 추천 사유
        );
    }
}
