package com.likelion.server.domain.recommendedStartupSupport.web.controller;

import com.likelion.server.domain.recommendedStartupSupport.service.RecommendedStartupSupportService;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportDetailResponse;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportSummaryResponse;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class RecommendedStartupSupportController {
    private final RecommendedStartupSupportService recommendedStartupSupportService;

    @GetMapping("/recommendations/{recommendedId}")
    public SuccessResponse<RecommendedStartupSupportDetailResponse> getById(
            @PathVariable("recommendedId") Long recommendedId
    ){
        return SuccessResponse.ok(
                recommendedStartupSupportService.getById(recommendedId));
    }

    // 레포트 기반 추천 창업 지원사업 조회
    @GetMapping("/reports/{reportId}/recommendations")
    public SuccessResponse<List<RecommendedStartupSupportSummaryResponse>> getByReportId(
            @PathVariable Long reportId
    ) {
        return SuccessResponse.ok(
                recommendedStartupSupportService.getByReportId(reportId)
        );
    }
}
