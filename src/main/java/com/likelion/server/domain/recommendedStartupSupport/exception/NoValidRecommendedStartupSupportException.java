package com.likelion.server.domain.recommendedStartupSupport.exception;

import com.likelion.server.global.exception.BaseException;

public class NoValidRecommendedStartupSupportException extends BaseException {
  public NoValidRecommendedStartupSupportException() {
    super(RecommendedStartupSupportErrorCode.NO_VALID_RECOMMENDED_STARTUP_SUPPORT);
  }
}
