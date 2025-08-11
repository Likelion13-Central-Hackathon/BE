package com.likelion.server.domain.idea.exception;

import com.likelion.server.global.exception.BaseException;

// 잘못된 Enum 값 전달
public class IdeaInvalidEnumException extends BaseException {
  public IdeaInvalidEnumException() {
    super(IdeaErrorCode.IDEA_400_INVALID_ENUM);
  }
}
