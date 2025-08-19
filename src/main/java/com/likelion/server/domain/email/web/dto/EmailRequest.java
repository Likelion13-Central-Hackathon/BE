package com.likelion.server.domain.email.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmailRequest(
        @NotBlank
        @Email
        String email,

        @NotBlank
        String password,

        @NotNull
        Long ideaId,

        Boolean isEnrolled
) {}