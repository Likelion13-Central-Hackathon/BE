package com.likelion.server.domain.idea.web.controller;

import com.likelion.server.domain.idea.service.IdeaService;
import com.likelion.server.domain.idea.web.dto.CreateIdeaRequest;
import com.likelion.server.domain.idea.web.dto.CreateIdeaResponse;
import com.likelion.server.domain.report.service.ReportService;
import com.likelion.server.domain.report.web.dto.ReportCreateResponse;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ideas")
public class IdeaController {
    private final IdeaService ideaService;
    private final ReportService reportService;

    // 창업 아이디어 생성
    @PostMapping
    public SuccessResponse<CreateIdeaResponse> createIdea(@RequestBody @Valid CreateIdeaRequest createIdeaRequest){
        CreateIdeaResponse data = ideaService.create(createIdeaRequest);
        return SuccessResponse.created(data);
    }

    // 창업 아이디어를 토대로 레포트 생성
    @PostMapping("/ideas/{ideaId}/reports")
    public SuccessResponse<ReportCreateResponse> createReport(
            @PathVariable("ideaId") Long ideaId
    ) {
        ReportCreateResponse data = reportService.createReportForIdea(ideaId);
        return SuccessResponse.created(data);
    }

}
