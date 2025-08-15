package com.likelion.server.domain.admin.web.controller;

import com.likelion.server.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // 청년 창업 지원 사업 수집
    @PostMapping("/startup-supports/batch")
    public void batchStartup() {
        adminService.syncLatestStartupSupports();
    }
}
