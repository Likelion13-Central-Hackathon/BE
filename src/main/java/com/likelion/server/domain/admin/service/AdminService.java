package com.likelion.server.domain.admin.service;

import com.likelion.server.domain.admin.web.dto.StartupSupportResponse;

import java.util.List;

public interface AdminService {
    // 최신 창업 지원사업 데이터를 수집 및 동기화
    List<StartupSupportResponse> syncLatestStartupSupports();
}
