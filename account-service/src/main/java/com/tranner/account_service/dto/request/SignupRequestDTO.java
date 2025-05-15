package com.tranner.account_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SignupRequestDTO(

        @NotBlank(message = "아이디를 입력해주세요")
        @Pattern(
                regexp = "^[a-zA-Z0-9]{5,20}$",
                message = "아이디는 영문자 또는 숫자 5~20자리여야 합니다."
        )
        String memberId,

        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()_+=])[A-Za-z\\d~!@#$%^&*()_+=]{8,20}$",
                message = "비밀번호는 영문자, 숫자, 특수문자를 포함한 8~20자리여야 합니다."
        )
        String password,

        @NotBlank(message = "이메일을 입력해주세요")
        @Email(message = "유효한 이메일 주소를 입력해주세요")
        String memberEmail


) { }

