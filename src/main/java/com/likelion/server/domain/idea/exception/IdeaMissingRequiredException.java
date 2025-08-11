package com.likelion.server.domain.idea.exception;

import com.likelion.server.global.exception.BaseException;

// 필수 값 누락
public class IdeaMissingRequiredException extends BaseException {
    public IdeaMissingRequiredException() {
        super(IdeaErrorCode.IDEA_400_MISSING_REQUIRED);
    }
}
