package com.likelion.server.domain.answer.service;

import com.likelion.server.domain.answer.web.dto.QaResponse;

import java.util.List;

public interface QaService {
    List<QaResponse> generateByAnswerId(Long answerId);
}
