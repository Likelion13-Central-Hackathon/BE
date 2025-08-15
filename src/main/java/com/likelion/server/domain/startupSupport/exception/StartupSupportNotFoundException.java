package com.likelion.server.domain.startupSupport.exception;

import com.likelion.server.global.exception.BaseException;

public class StartupSupportNotFoundException extends BaseException {
  public StartupSupportNotFoundException() {
    super(StartupSupportErrorCode.STARTUP_SUPPORT_404_NOT_FOUND);
  }
}
