package com.example.community.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public abstract class AuthenticatedRequestDto {
    @NotBlank(message = "Access Token is required")
    protected String accessToken;
}
