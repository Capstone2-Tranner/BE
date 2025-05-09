package com.tranner.account_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailVerificationRequestDTO (
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "유효한 이메일 주소를 입력해주세요")
        String email,

        @NotBlank(message = "인증코드를 입력해주세요")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "인증코드는 6자리입니다."
        )
        String verificationCode
){ }
