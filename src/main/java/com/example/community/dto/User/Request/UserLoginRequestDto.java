package com.example.community.dto.User.Request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class UserLoginRequestDto extends UserAuthRequestDto {
    @Builder
    public UserLoginRequestDto(String email, String password) {
        super(email, password);
    }
}
