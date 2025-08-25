package com.likelion.server.domain.email.service;

import com.likelion.server.domain.email.web.dto.EmailRequest;

public interface EmailService {
    void subscribe(EmailRequest req);
}
