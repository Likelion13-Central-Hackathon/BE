package com.likelion.server.domain.idea.exception;

import com.likelion.server.domain.report.exception.ReportErrorCode;
import com.likelion.server.global.exception.BaseException;

public class IdeaNotFoundException extends BaseException {
    public IdeaNotFoundException() {super(ReportErrorCode.IDEA_404_NOT_FOUND);}
}
