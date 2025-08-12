package com.likelion.server.domain.report.web.controller;


import com.likelion.server.domain.report.service.ReportQueryService;
import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.LatestReportDetailResponse;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportQueryController {

    private final ReportQueryService reportQueryService;

    @GetMapping("/lastest")
    public SuccessResponse<LatestReportDetailResponse> getLatestReport(
            @RequestBody @Valid LatestReportDetailRequest request
    ) {
        LatestReportDetailResponse data = reportQueryService.getLatestReport(request);
        return SuccessResponse.ok(data);
    }
}