package com.likelion.server.domain.startupSupport.service;

import com.likelion.server.domain.startupSupport.web.dto.StartupSupportSummaryResponse;
import org.springframework.data.domain.Page;

public interface StartupSupportService {
    public Page<StartupSupportSummaryResponse> getPagedOpenSupports(String regionParam, int page, int num);
}
