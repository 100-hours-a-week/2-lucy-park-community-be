package com.example.community.dto.User.Request;

import com.example.community.dto.Auth.AuthenticatedRequestDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLogoutRequestDto extends AuthenticatedRequestDto {
}
