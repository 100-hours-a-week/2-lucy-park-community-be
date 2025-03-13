package com.example.community.security;

import com.example.community.entity.User;
import com.example.community.repository.UserRepository;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtUtil {
    private final PrivateKey privateKey;
    private final PublicKey publicKey;
    private UserRepository userRepository;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60; // 테스트 위해 길게 설정, 수정 필요
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 60;

    public JwtUtil(@Value("${JWT_PRIVATE_KEY}") String privateKey,
                   @Value("${JWT_PUBLIC_KEY}") String publicKey,
                   UserRepository userRepository, JwtAuthenticationFilter jwtAuthenticationFilter, JwtTokenProvider jwtTokenProvider) throws Exception {
        this.privateKey = loadPrivateKey(privateKey);
        this.publicKey = loadPublicKey(publicKey);
        this.userRepository = userRepository;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Base64 인코딩된 문자열을 PrivateKey 객체로 변환
     */
    private PrivateKey loadPrivateKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    /**
     * Base64 인코딩된 문자열을 PublicKey 객체로 변환
     */
    private PublicKey loadPublicKey(String base64Key) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    /**
     * JWT 토큰 생성 (RS256 적용)
     */
    public String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(privateKey, SignatureAlgorithm.RS256) // RS256 적용
                .compact();
    }

    public String generateRefreshToken(User user) {
        return Jwts.builder()
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(privateKey, SignatureAlgorithm.RS256) // RS256 적용
                .compact();
    }

    // 회원 조회
    public User verifyUser(HttpServletRequest request) {

        String token = jwtAuthenticationFilter.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new RuntimeException("❌ 유효하지 않은 JWT 토큰입니다.");
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 유저입니다."));

        return user;
    }
}
