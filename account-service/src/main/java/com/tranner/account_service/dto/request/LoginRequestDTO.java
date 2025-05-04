package com.tranner.account_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDTO (
        @NotBlank(message = "아이디를 입력해주세요")
        String memberId,

        @NotBlank(message = "비밀번호를 입력해주세요")
        String password

) {}
