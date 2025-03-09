package com.example.community.dto.User.Request;

import com.example.community.dto.Auth.AuthenticatedRequestDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateProfileImageRequestDto extends AuthenticatedRequestDto {
    @NotBlank(message = "이미지를 첨부해주세요.")
    private String imageUrl;
}
