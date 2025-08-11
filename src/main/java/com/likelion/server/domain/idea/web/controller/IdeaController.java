package com.likelion.server.domain.idea.web.controller;

import com.likelion.server.domain.idea.service.IdeaService;
import com.likelion.server.domain.idea.web.dto.CreateIdeaRequest;
import com.likelion.server.domain.idea.web.dto.CreateIdeaResponse;
import com.likelion.server.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ideas")
public class IdeaController {
    private final IdeaService ideaService;

    @PostMapping
    public SuccessResponse<CreateIdeaResponse> createIdea(@RequestBody CreateIdeaRequest createIdeaRequest){
        CreateIdeaResponse data = ideaService.create(createIdeaRequest);
        return SuccessResponse.created(data);
    }

}
