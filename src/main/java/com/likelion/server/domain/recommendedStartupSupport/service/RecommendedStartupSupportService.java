package com.likelion.server.domain.recommendedStartupSupport.service;

import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportDetailResponse;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportSummaryResponse;

import java.util.List;

public interface RecommendedStartupSupportService {
    // 레포트 기반 추천 창업 지원 사업 목록 조회
    public List<RecommendedStartupSupportSummaryResponse> getByReportId(Long reportId);
    // 추천 창업 지원 사업 상세 조회
    public RecommendedStartupSupportDetailResponse getById(Long recommendedStartupSupportId);
}
