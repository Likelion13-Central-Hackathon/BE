package com.likelion.server.domain.answer.exception;

import com.likelion.server.global.exception.BaseException;

public class AnswersNotFoundException extends BaseException {
    public AnswersNotFoundException() {super(AnswerErrorCode.ANSWERS_404_NOT_FOUND);}
}