package com.likelion.server.domain.report.web.controller;


import com.likelion.server.domain.recommendedStartupSupport.service.RecommendedStartupSupportService;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportDetailResponse;
import com.likelion.server.domain.report.service.ReportService;
import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.ReportDetailResponse;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;
    private final RecommendedStartupSupportService recommendedStartupSupportService;

    // 최근 리포트 상세 조회
    @GetMapping("/latest")
    public SuccessResponse<ReportDetailResponse> getLatestReport(
            @RequestBody @Valid LatestReportDetailRequest request
    ) {
        ReportDetailResponse data = reportService.getLatestReport(request);
        return SuccessResponse.ok(data);
    }

    // ID 기반 상세 조회
    @GetMapping("/{reportId}")
    public SuccessResponse<ReportDetailResponse> getById(@PathVariable Long reportId) {
        return SuccessResponse.ok(reportService.getById(reportId));
    }

    // 레포트 기반 추천 창업 지원사업 조회

}