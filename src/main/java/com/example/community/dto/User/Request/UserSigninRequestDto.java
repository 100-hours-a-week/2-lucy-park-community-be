package com.example.community.dto.User.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserSigninRequestDto extends UserAuthRequestDto {
    @NotBlank(message = "nickname is required")
    @Size(min = 2, max = 20, message = "닉네임은 10자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "이미지를 업로드한 후 URL을 입력해주세요.")
    private String imageUrl;

    @Builder
    public UserSigninRequestDto(String email, String password, String nickname, String imageUrl) {
        super(email, password);
        this.nickname = nickname;
        this.imageUrl = imageUrl;
    }
}
