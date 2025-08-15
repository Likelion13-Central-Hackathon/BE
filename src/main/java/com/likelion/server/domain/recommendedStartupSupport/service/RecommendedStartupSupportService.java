package com.likelion.server.domain.recommendedStartupSupport.service;

import com.likelion.server.domain.recommendedStartupSupport.web.dto.StartupSupportDetailResponse;

public interface RecommendedStartupSupportService {
    // 추천 창업 지원 사업 상세 조회
    public StartupSupportDetailResponse getDetailSupports(Long recommendedStartupSupportId);
}
