package com.likelion.server.domain.answer.service;

import java.util.Map;

public interface QaService {
    Map<String, Object> generateByAnswerId(Long answerId);
}
