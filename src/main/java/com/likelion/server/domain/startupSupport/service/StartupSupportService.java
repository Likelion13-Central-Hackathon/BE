package com.likelion.server.domain.startupSupport.service;

import com.likelion.server.domain.startupSupport.web.dto.StartupSupportSummaryResponse;

import java.util.List;

public interface StartupSupportService {
    public List<StartupSupportSummaryResponse> getPagedOpenSupports(String regionParam, int page, int num);
}
