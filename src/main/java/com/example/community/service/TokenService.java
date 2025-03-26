package com.example.community.service;

import com.example.community.entity.RefreshToken;
import com.example.community.entity.User;
import com.example.community.repository.RefreshTokenRepository;
import com.example.community.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    public String generateTokensForUser(User user) {
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        RefreshToken newToken = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .build();

        refreshTokenRepository.findByUser(user).ifPresentOrElse(
                existingToken -> existingToken.updateToken(refreshToken),
                () -> refreshTokenRepository.save(newToken));

        return accessToken;
    }

    public void expireRefreshTokenForUser(User user) {
        if(user.getRefreshToken() != null) {
            user.getRefreshToken().expireToken();
            refreshTokenRepository.save(user.getRefreshToken());
        }
    }
}
