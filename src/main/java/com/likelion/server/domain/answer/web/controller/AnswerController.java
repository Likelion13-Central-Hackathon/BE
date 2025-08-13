package com.likelion.server.domain.answer.web.controller;

import com.likelion.server.domain.answer.service.AnswerService;
import com.likelion.server.domain.answer.web.dto.AnswerRequest;
import com.likelion.server.domain.answer.web.dto.AnswerResponse;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/answers")
public class AnswerController {

    private final AnswerService answerService;

    @PostMapping
    public ResponseEntity<SuccessResponse<AnswerResponse>> create(@Valid @RequestBody AnswerRequest req) {
        var result = answerService.correctQuestion(req.questionNumber(), req.userAnswer());
        var data = new AnswerResponse(result.aiAnswer(), result.answerId());

        return ResponseEntity.ok(SuccessResponse.ok(data));
    }
}
