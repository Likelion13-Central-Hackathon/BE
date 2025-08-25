package com.likelion.server.domain.startupSupport.web.controller;

import com.likelion.server.domain.startupSupport.service.StartupSupportService;
import com.likelion.server.domain.startupSupport.web.dto.StartupSupportSummaryResponse;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/startup-supports")
public class StartupSupportController {
    private final StartupSupportService startupSupportService;

    // 창업 지원 사업 목록 조회
    // 최신순(마감 제외) +페이징 + 지역 필터(요청 지역 + 전국 포함)
    @GetMapping
    public SuccessResponse<?> getPagedOpenSupports(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "num", defaultValue = "5") int num,
            @RequestParam(name = "region") String region
    ) {
        List<StartupSupportSummaryResponse> data = startupSupportService.getPagedOpenSupports(region, page, num);
        return SuccessResponse.ok(data);
    }

}
