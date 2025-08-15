package com.likelion.server.domain.report.service;

import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.ReportCreateResponse;
import com.likelion.server.domain.report.web.dto.ReportDetailResponse;
import com.likelion.server.domain.startupSupport.web.dto.StartupSupportDetailResponse;

public interface ReportService {
    ReportCreateResponse createReport(Long ideaId);

    ReportDetailResponse getLatestReport(LatestReportDetailRequest request);

    ReportDetailResponse getById(Long reportId);

    // 추천 창업 지원 사업 상세 조회
    public StartupSupportDetailResponse getDetailSupports(Long reportId, Long supportId);
}
