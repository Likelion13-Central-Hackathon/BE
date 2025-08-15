package com.likelion.server.domain.report.service;

import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.ReportCreateResponse;
import com.likelion.server.domain.report.web.dto.ReportDetailResponse;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.StartupSupportDetailResponse;

public interface ReportService {
    ReportCreateResponse createReport(Long ideaId);

    ReportDetailResponse getLatestReport(LatestReportDetailRequest request);

    ReportDetailResponse getById(Long reportId);
}
