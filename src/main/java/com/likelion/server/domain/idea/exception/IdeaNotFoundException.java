package com.likelion.server.domain.idea.exception;

import com.likelion.server.global.exception.BaseException;

public class IdeaNotFoundException extends BaseException {
    public IdeaNotFoundException() {super(IdeaErrorCode.IDEA_404_NOT_FOUND);}
}
