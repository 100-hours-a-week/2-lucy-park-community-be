package com.example.community.dto.User.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginResponseDto {
    private Long id;
    private String email;
    private String nickname;
    private String imageUrl;
    private String accessToken;
}
