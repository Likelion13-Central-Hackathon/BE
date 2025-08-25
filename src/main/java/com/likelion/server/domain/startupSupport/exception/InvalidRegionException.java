package com.likelion.server.domain.startupSupport.exception;

import com.likelion.server.global.exception.BaseException;

public class InvalidRegionException extends BaseException {
  public InvalidRegionException() {
    super(StartupSupportErrorCode.REGION_400_INVALID_VALUE);
  }
}
