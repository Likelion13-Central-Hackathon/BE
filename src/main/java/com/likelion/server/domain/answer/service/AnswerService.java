package com.likelion.server.domain.answer.service;

public interface AnswerService {
    AnswerResult correctQuestion (int questionNumber, String userAnswer);
}
