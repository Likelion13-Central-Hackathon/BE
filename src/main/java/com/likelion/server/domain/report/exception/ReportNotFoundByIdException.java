package com.likelion.server.domain.report.exception;

import com.likelion.server.global.exception.BaseException;

public class ReportNotFoundByIdException extends BaseException {
    public ReportNotFoundByIdException() {
        super(ReportErrorCode.REPORT_404_NOT_FOUND_BY_ID);
    }
}
