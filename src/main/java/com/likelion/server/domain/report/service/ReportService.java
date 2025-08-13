package com.likelion.server.domain.report.service;

import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.ReportDetailResponse;

public interface ReportService {
    ReportDetailResponse getLatestReport(LatestReportDetailRequest request);

    ReportDetailResponse getById(Long reportId);
}
