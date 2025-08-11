package com.likelion.server.domain.report.exception;

import com.likelion.server.global.exception.BaseException;

public class AuthFailException extends BaseException {
    public AuthFailException() {super(AuthErrorCode.AUTH_401_INVALID_CREDENTIALS);}
}
