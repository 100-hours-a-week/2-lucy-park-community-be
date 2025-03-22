package com.example.community.dto.User.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class UserAuthRequestDto {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    @NotBlank(message = "email is required")
    protected String email;

    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\W).{8,20}$",
            message = "비밀번호는 대문자, 소문자, 특수문자를 포함하여 8~20자여야 합니다."
    )
    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상, 20자 이하로 입력해주세요.")
    protected String password;
}
