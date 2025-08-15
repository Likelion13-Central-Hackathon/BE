package com.likelion.server.domain.recommendedStartupSupport.web.dto;

import com.likelion.server.domain.recommendedStartupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;

public record RecommendedStartupSupportDetailResponse(
        String supportArea, // 지원 분야
        String title, // 제목
        String link, // 본문 접속을 위한 링크
        String startDate, // YYYY.MM.DD
        String endDate, // YYYY.MM.DD
        String region,
        String businessDuration, // 업력
        String agency, // 주관기관
        String targetAge, // 나이 범위
        String target, // 대상 범위
        String contact, // 연락처
        String applyMethod, //신청 방법
        String supportDetails, //지원 내용
        String requiredDocuments, //제출 서류
        String applyProcedureAndEvaluation, //신청 절차 및 평가 방법
        int suitability, // 적합도
        String reason // 추천한 이유
) {
    public static RecommendedStartupSupportDetailResponse of(StartupSupport s, RecommendedStartupSupport r) {
        return new RecommendedStartupSupportDetailResponse(
                s.getSupportArea(),
                s.getTitle(),
                s.getLink(),
                s.getStartDate(),
                s.getEndDate(),
                s.getRegion(),
                s.getBusinessDuration(), // 업력
                s.getAgency(),
                s.getTarget(),
                s.getContact(),// 연락처
                s.getApplyMethod(), // 신청방법
                s.getSupportDetails(), // 지원 내용
                s.getRequiredDocuments(), // 제출 서류
                s.getApplyProcedure(),
                s.getEvaluationMethod(),
                r.getSuitability(), // 적합도
                r.getReason() // 추천한 이유
        );
    }
}