package com.likelion.server.domain.report.web.controller;


import com.likelion.server.domain.report.service.ReportService;
import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.ReportCreateResponse;
import com.likelion.server.domain.report.web.dto.ReportDetailResponse;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping()
public class ReportController {

    private final ReportService reportService;

    // 레포트 생성
    @PostMapping("/ideas/{ideaId}/reports")
    public SuccessResponse<ReportCreateResponse> createReport(
            @RequestParam("ideaId") Long ideaId
    ) {
        return null;
    }


    // 최근 리포트 상세 조회
    @GetMapping("/reports/latest")
    public SuccessResponse<ReportDetailResponse> getLatestReport(
            @RequestBody @Valid LatestReportDetailRequest request
    ) {
        ReportDetailResponse data = reportService.getLatestReport(request);
        return SuccessResponse.ok(data);
    }

    // ID 기반 상세 조회
    @GetMapping("/reports/{reportId}")
    public SuccessResponse<ReportDetailResponse> getById(@PathVariable Long reportId) {
        return SuccessResponse.ok(reportService.getById(reportId));
    }
}