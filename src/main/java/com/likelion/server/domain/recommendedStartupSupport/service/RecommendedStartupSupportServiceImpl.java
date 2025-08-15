package com.likelion.server.domain.recommendedStartupSupport.service;

import com.likelion.server.domain.recommendedStartupSupport.repository.RecommendedStartupSupportRepository;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.StartupSupportDetailResponse;
import com.likelion.server.domain.report.entity.Report;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendedStartupSupportServiceImpl implements RecommendedStartupSupportService {
    private final RecommendedStartupSupportRepository recommendedStartupSupportRepository;

    // 추천 창업 지원 사업 상세 조회
    @Override
    public StartupSupportDetailResponse getDetailSupports(Long recommendedStartupSupportId) {
        // 404: 레포트 찾을 수 없음
        Report report = recommendedStartupSupportRepository.findById(recommendedStartupSupportId)
                .orElseThrow(RecommendedStartupSupportNotFoundException::new);

        //return new StartupSupportDetailResponse(
        //                support.getSupportArea(),
        //                support.getTitle(),
        //                support.getLink(),
        //                support.getStartDate(),
        //                support.getEndDate(),
        //                support.getRegion(),
        //                support.getBusinessDuration(), // 업력
        //                support.getAgency(),
        //                support.getTarget(),
        //                support.getContact(),// 연락처
        //                support.getApplyMethod(), // 신청방법
        //                support.getSupportDetails(), // 지원 내용
        //                support.getRequiredDocuments(), // 제출 서류
        //                support.getApplyProcedure(),
        //                support.getEvaluationMethod(),
        //
        //                //        int suitability, // 적합도
        //                //        String reason // 추천한 이유
        //        );
    }


}
