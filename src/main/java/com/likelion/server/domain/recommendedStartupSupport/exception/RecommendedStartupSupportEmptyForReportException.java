package com.likelion.server.domain.recommendedStartupSupport.exception;

import com.likelion.server.global.exception.BaseException;

public class RecommendedStartupSupportEmptyForReportException extends BaseException {
  public RecommendedStartupSupportEmptyForReportException() {
    super(RecommendedStartupSupportErrorCode.RECOMMENDED_STARTUP_SUPPORT_404_EMPTY_FOR_REPORT);
  }
}