package com.likelion.server.domain.recommendedStartupSupport.exception;

import com.likelion.server.global.exception.BaseException;

public class RecommendedStartupSupportNotFoundException extends BaseException {
  public RecommendedStartupSupportNotFoundException() {
    super(RecommendedStartupSupportErrorCode.RECOMMENDED_STARTUP_SUPPORT_404_NOT_FOUND);
  }
}
