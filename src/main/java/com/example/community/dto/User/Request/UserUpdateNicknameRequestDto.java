package com.example.community.dto.User.Request;

import com.example.community.dto.Auth.AuthenticatedRequestDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateNicknameRequestDto extends AuthenticatedRequestDto {
    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min = 8, max = 20, message = "닉네임은 10자 이하로 입력해주세요.")
    private String nickname;
}
