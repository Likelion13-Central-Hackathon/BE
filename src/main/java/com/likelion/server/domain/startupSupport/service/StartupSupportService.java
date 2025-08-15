package com.likelion.server.domain.startupSupport.service;

import com.likelion.server.domain.startupSupport.web.dto.StartupSupportDetailResponse;
import com.likelion.server.domain.startupSupport.web.dto.StartupSupportSummaryResponse;

import java.util.List;

public interface StartupSupportService {
    // 창업 지원 사업 목록 조회
    public List<StartupSupportSummaryResponse> getPagedOpenSupports(String regionParam, int page, int num);
}
