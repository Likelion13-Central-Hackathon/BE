package com.likelion.server.domain.recommendedStartupSupport.web.controller;

import com.likelion.server.domain.recommendedStartupSupport.service.RecommendedStartupSupportService;
import com.likelion.server.domain.recommendedStartupSupport.web.dto.RecommendedStartupSupportDetailResponse;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommendations")
@RequiredArgsConstructor
public class RecommendedStartupSupportController {
    private final RecommendedStartupSupportService recommendedStartupSupportService;

    @GetMapping("/{recommendedId}")
    public SuccessResponse<RecommendedStartupSupportDetailResponse> getById(
            @PathVariable("recommendedId") Long recommendedId
    ){
        RecommendedStartupSupportDetailResponse data = recommendedStartupSupportService.getDetailSupports(recommendedId);
        return SuccessResponse.ok(data);
    }
}
