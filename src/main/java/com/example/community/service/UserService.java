package com.example.community.service;

import com.example.community.dto.User.Request.*;
import com.example.community.dto.User.Response.UserLoginResponseDto;
import com.example.community.dto.User.Response.UserUpdateProfileImageResponseDto;
import com.example.community.entity.RefreshToken;
import com.example.community.entity.User;
import com.example.community.repository.RefreshTokenRepository;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtAuthenticationFilter;
import com.example.community.security.JwtTokenProvider;
import com.example.community.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtUtil jwtUtil,
                       JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtUtil = jwtUtil;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 회원가입
    public User registerUser(UserSigninRequestDto requestDto) {
        if(userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if(requestDto.getImageUrl() == null) {
            throw new IllegalArgumentException("이미지를 먼저 업로드해주세요.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = User.builder()
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .nickname(requestDto.getNickname())
                .imageUrl(requestDto.getImageUrl())
                .build();

        return userRepository.save(user);
    }

    // 로그인
    public UserLoginResponseDto loginUser(UserLoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 이용자입니다."));

        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        RefreshToken newToken = RefreshToken.builder()
                        .user(user)
                        .token(refreshToken)
                        .build();

        refreshTokenRepository.findByUser(user).ifPresentOrElse(
                existingToken -> existingToken.updateToken(refreshToken),
                () -> refreshTokenRepository.save(newToken));
                //.orElseGet(() -> refreshTokenRepository.save(newToken));


        return UserLoginResponseDto.builder()
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .accessToken(accessToken)
                .build();
    }

    // 회원 프로필 사진 수정
    public UserUpdateProfileImageResponseDto updateProfileImage(UserUpdateProfileImageRequestDto requestDto, HttpServletRequest request) {

        User user = jwtUtil.verifyUser(request);
        if(requestDto.getImageUrl() != null) {
            user.setImageUrl(requestDto.getImageUrl());
        }

        return UserUpdateProfileImageResponseDto.builder()
                .imageUrl(user.getImageUrl())
                .build();
    }

    //
    public User updateNickname(UserUpdateNicknameRequestDto requestDto, HttpServletRequest request) {

        User user = jwtUtil.verifyUser(request);
        if(requestDto.getNickname() != null) {
            user.setNickname(requestDto.getNickname());
        }
        return user;
    }

    // 비밀번호
    public User updatePassword(UserUpdatePasswordRequestDto requestDto, HttpServletRequest request) {

        User user = jwtUtil.verifyUser(request);
        if(requestDto.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
            user.setPassword(encodedPassword);
        }
        return user;
    }
}
