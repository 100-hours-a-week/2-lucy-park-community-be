package com.example.community.dto.Auth;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogoutRequestDto extends AuthenticatedRequestDto {
    private Long id;
}
