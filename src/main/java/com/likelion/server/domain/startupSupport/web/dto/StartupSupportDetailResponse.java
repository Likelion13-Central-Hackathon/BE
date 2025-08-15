package com.likelion.server.domain.startupSupport.web.dto;

public record StartupSupportDetailResponse(
        String supportArea, // 분야
        String title, // 제목
        String link, // 본문 접속을 위한 링크
        String startDate,
        String endDate,
        String region,
        String businessDuration, // 업력
        String agency, // 주관기관
        String target, //
        String contact, // 연락처
        String coreContent, // 핵심 본문 내용(신청방법 및 대상, 지원 내용, 제출 서류, 신청 절차 및 평가 방법)
        int suitability, // 적합도
        String reason // 추천한 이유
) {
}