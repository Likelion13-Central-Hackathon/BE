package com.likelion.server.domain.email.web.controller;

import com.likelion.server.domain.email.service.EmailService;
import com.likelion.server.domain.email.web.dto.EmailRequest;
import com.likelion.server.global.response.SuccessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email-subscriptions")
@RequiredArgsConstructor
public class EmailController {

    private final EmailService service;

    @PostMapping
    public SuccessResponse<Void> subscribe(@RequestBody @Valid EmailRequest req) {
        service.subscribe(req);
        return SuccessResponse.empty();
    }
}