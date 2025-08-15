package com.likelion.server.domain.recommendedStartupSupport.service;

import com.likelion.server.domain.recommendedStartupSupport.entity.RecommendedStartupSupport;
import com.likelion.server.domain.recommendedStartupSupport.exception.RecommendedStartupSupportNotFoundException;
import com.likelion.server.domain.recommendedStartupSupport.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportDetailResponse;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportSummaryResponse;
import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendedStartupSupportServiceImpl implements RecommendedStartupSupportService {
    private final RecommendedStartupSupportRepository recommendedStartupSupportRepository;
    // 레포트 기반 추천 창업 지원 사업 목록 조회
    @Override
    public List<RecommendedStartupSupportSummaryResponse> getByReportId(Long reportId) {
        // DB 조회(상위 3개, 레포트 ID 기반, 적합도 기반 내림차순 정렬)
        List<RecommendedStartupSupport> supportList = recommendedStartupSupportRepository.findTop3ByReportIdOrderBySuitabilityDesc((reportId));

        // Entity -> DTO
        return supportList.stream()
                .map(s -> new RecommendedStartupSupportSummaryResponse(
                        s.getId(),
                        s.getSuitability(),
                        s.getStartupSupport().getSupportArea(),
                        s.getStartupSupport().getTitle(),
                        s.getStartupSupport().getAgency(),
                        s.formatYmd(s.getStartupSupport().getStartDate()),
                        s.formatYmd(s.getStartupSupport().getEndDate())
                ))
                .toList();
    }

    // 추천 창업 지원 사업 상세 조회
    @Override
    public RecommendedStartupSupportDetailResponse getById(Long recommendedStartupSupportId) {
        // 404: 추천 창업 지원 사업 찾을 수 없음
        RecommendedStartupSupport recommendedStartupSupport = recommendedStartupSupportRepository.findByIdWithSupport(recommendedStartupSupportId)
                .orElseThrow(RecommendedStartupSupportNotFoundException::new);
        StartupSupport startupSupport = recommendedStartupSupport.getStartupSupport();

        // Entity -> DTO
        return RecommendedStartupSupportDetailResponse.of(
                startupSupport,
                recommendedStartupSupport);
    }
}
