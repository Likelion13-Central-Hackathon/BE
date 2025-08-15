package com.likelion.server.domain.startupSupport.web.dto;

public record StartupSupportDetailResponse(
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
}