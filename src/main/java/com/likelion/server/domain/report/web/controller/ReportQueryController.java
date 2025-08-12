package com.likelion.server.domain.report.web.controller;


import com.likelion.server.domain.report.service.ReportQueryService;
import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.ReportDetailResponse;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportQueryController {

    private final ReportQueryService reportQueryService;

    // 최근 리포트 상세 조회
    @GetMapping("/lastest")
    public SuccessResponse<ReportDetailResponse> getLatestReport(
            @RequestBody @Valid LatestReportDetailRequest request
    ) {
        ReportDetailResponse data = reportQueryService.getLatestReport(request);
        return SuccessResponse.ok(data);
    }

    // ID 기반 상세 조회
    @GetMapping("/{reportId}")
    public SuccessResponse<ReportDetailResponse> getById(@PathVariable Long reportId) {
        return SuccessResponse.ok(reportQueryService.getById(reportId));
    }
}