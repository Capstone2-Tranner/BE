package com.tranner.account_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LoginRequestDTO (
        @NotBlank(message = "아이디를 입력해주세요")
        String memberId,

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[A-Za-z\\d~!@#$%^&*()_+=]{8,20}$",
                message = "비밀번호는 영문자, 숫자, 특수문자를 포함한 8~20자리여야 합니다."
        )
        String password

) {}
