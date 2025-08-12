package com.likelion.server.domain.report.service;

import com.likelion.server.domain.report.web.dto.LatestReportDetailRequest;
import com.likelion.server.domain.report.web.dto.LatestReportDetailResponse;

public interface ReportQueryService {
    LatestReportDetailResponse getLatestReport(LatestReportDetailRequest request);
}
