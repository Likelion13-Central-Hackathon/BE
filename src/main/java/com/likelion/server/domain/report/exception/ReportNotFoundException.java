package com.likelion.server.domain.report.exception;

import com.likelion.server.global.exception.BaseException;

public class ReportNotFoundException extends BaseException {
    public ReportNotFoundException() {super(ReportErrorCode.REPORT_404_NOT_FOUND);}
}
