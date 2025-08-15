package com.likelion.server.domain.admin.service;

import com.likelion.server.domain.startupSupport.entity.StartupSupport;
import com.likelion.server.domain.startupSupport.repository.StartupSupportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService{

    private final StartupSupportRepository supportRepository;
    // 최신 창업 지원사업 데이터를 수집 및 동기화
    @Override
    public void syncLatestStartupSupports() {

    }
}
