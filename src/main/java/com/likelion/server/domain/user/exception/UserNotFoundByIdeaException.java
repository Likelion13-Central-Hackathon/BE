package com.likelion.server.domain.user.exception;

import com.likelion.server.global.exception.BaseException;

public class UserNotFoundByIdeaException extends BaseException {
    public UserNotFoundByIdeaException() {
        super(UserErrorCode.USER_404_NOT_FOUND_BY_IDEA); 
    }
}
