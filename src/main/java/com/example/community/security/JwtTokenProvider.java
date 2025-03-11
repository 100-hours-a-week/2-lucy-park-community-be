package com.example.community.security;

import com.example.community.dto.User.Request.UserUpdateProfileImageRequestDto;
import com.example.community.dto.User.Response.UserUpdateProfileImageResponseDto;
import com.example.community.entity.User;
import com.example.community.repository.UserRepository;
import com.example.community.service.CustomUserDetailsServiceImpl;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final PublicKey publicKey;
    private UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsServiceImpl customUserDetailsService;

    public JwtTokenProvider(@Value("${JWT_PUBLIC_KEY}") String publicKeyStr, UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter, CustomUserDetailsServiceImpl customUserDetailsService) {
        this.publicKey = loadPublicKey(publicKeyStr);
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    // 🔥 Base64로 인코딩된 공개키를 PublicKey 객체로 변환
    private PublicKey loadPublicKey(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new RuntimeException("❌ 공개키 로딩 실패: " + e.getMessage(), e);
        }
    }

    // 🔹 JWT 검증
    public boolean validateToken(String token) {
        try {
            System.out.println("🔍 검증 시작: " + token);
            System.out.println("🔍 공개키: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(token);

            System.out.println("✅ JWT 유효함: " + claims.getBody().getSubject());
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            System.out.println("❌ JWT 만료됨: " + e.getMessage());
        } catch (JwtException e) {
            System.out.println("❌ JWT 검증 실패: " + e.getMessage());
        }
        return false;
    }

    // userId 추출
    public Long getUserIdFromToken(String token) {
        return Long.parseLong(Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }

    // Authentication 객체 생성
    public Authentication getAuthentication(String token, UserDetailsService userDetailsService) {
        Long userId = getUserIdFromToken(token);
        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    // 회원 조회
    public User verifyUser(HttpServletRequest request) {

        String token = jwtAuthenticationFilter.resolveToken(request);
        if (token == null || validateToken(token)) {
            throw new RuntimeException("❌ 유효하지 않은 JWT 토큰입니다.");
        }

        Long userId = getUserIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        return user;
    }
}
