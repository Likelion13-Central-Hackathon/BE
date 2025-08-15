package com.likelion.server.domain.recommendedStartupSupport.service;

import com.likelion.server.domain.recommendedStartupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.recommendedStartupSupport.exception.RecommendedStartupSupportNotFoundException;
import com.likelion.server.domain.recommendedStartupSupport.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportDetailResponse;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendedStartupSupportServiceImpl implements RecommendedStartupSupportService {
    private final RecommendedStartupSupportRepository recommendedStartupSupportRepository;

    // 추천 창업 지원 사업 상세 조회
    @Override
    public RecommendedStartupSupportDetailResponse getDetailSupports(Long recommendedStartupSupportId) {
        // 404: 추천 창업 지원 사업 찾을 수 없음
        RecommendedStartupSupport recommendedStartupSupport = recommendedStartupSupportRepository.findByIdWithSupport(recommendedStartupSupportId)
                .orElseThrow(RecommendedStartupSupportNotFoundException::new);
        StartupSupport startupSupport = recommendedStartupSupport.getStartupSupport();

        return RecommendedStartupSupportDetailResponse.of(
                startupSupport,
                recommendedStartupSupport);
    }
}
