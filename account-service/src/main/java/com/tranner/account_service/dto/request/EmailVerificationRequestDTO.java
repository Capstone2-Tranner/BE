package com.tranner.account_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EmailVerificationRequestDTO (
        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "유효한 이메일 주소를 입력해주세요")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
                message = "이메일 형식이 올바르지 않습니다."
        )
        String email,

        @NotBlank(message = "인증코드를 입력해주세요")
        @Pattern(
                regexp = "^\\d{6}$",
                message = "인증코드는 6자리입니다."
        )
        String verificationCode
){ }
