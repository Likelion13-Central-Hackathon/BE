package com.likelion.server.domain.idea.exception;

import com.likelion.server.global.exception.BaseException;

// 대학 정보 관련 규칙 위반
public class UniversityConditionViolationException extends BaseException {
  public UniversityConditionViolationException() {
    super(IdeaErrorCode.IDEA_400_UNIVERSITY_CONDITION_VIOLATION);
  }
}
