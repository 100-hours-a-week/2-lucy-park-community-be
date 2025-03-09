package com.example.community.dto.User.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSigninRequestDto extends UserAuthRequestDto {
    @NotBlank(message = "nickname is required")
    @Size(min = 8, max = 20, message = "닉네임은 10자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "이미지를 첨부해주세요.")
    private String imageUrl;
}
