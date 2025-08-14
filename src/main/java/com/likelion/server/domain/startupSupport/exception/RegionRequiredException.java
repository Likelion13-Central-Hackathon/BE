package com.likelion.server.domain.startupSupport.exception;

import com.likelion.server.global.exception.BaseException;

public class RegionRequiredException extends BaseException {
  public RegionRequiredException() {
    super(StartupSupportErrorCode.REGION_400_REQUIRED);
  }
}
