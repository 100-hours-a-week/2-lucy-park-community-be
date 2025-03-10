package com.example.community.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
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
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 1000 * 60 * 10;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 1000 * 60 * 10;;

    public JwtUtil(@Value("${JWT_PRIVATE_KEY}") String privateKey,
                   @Value("${JWT_PUBLIC_KEY}") String publicKey) throws Exception {
        this.privateKey = loadPrivateKey(privateKey);
        this.publicKey = loadPublicKey(publicKey);
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
    public String generateAccessToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME))
                .signWith(privateKey, SignatureAlgorithm.RS256) // RS256 적용
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME))
                .signWith(privateKey, SignatureAlgorithm.RS256) // RS256 적용
                .compact();
    }

    /**
     * JWT 토큰 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

   /* *
     * 토큰에서 사용자 이메일 추출*/

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
