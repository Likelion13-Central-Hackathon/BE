package com.likelion.server.domain.recommendedStartupSupport.exception;

import com.likelion.server.global.exception.BaseException;

public class RecommendedStartupSupportCreatedException extends BaseException {
  public RecommendedStartupSupportCreatedException() {
    super(RecommendedStartupSupportErrorCode.RECOMMENDED_STARTUP_SUPPORT_500_CREATE_FAILED);
  }
}
