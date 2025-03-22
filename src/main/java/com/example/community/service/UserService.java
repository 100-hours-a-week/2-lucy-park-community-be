package com.example.community.service;

import com.example.community.dto.User.Request.*;
import com.example.community.dto.User.Response.UserLoginResponseDto;
import com.example.community.dto.User.Response.UserUpdateNicknameResponseDto;
import com.example.community.dto.User.Response.UserUpdateProfileImageResponseDto;
import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.RefreshToken;
import com.example.community.entity.User;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.RefreshTokenRepository;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtAuthenticationFilter;
import com.example.community.security.JwtTokenProvider;
import com.example.community.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public User registerUser(@Valid UserSigninRequestDto requestDto) {
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
    public UserLoginResponseDto loginUser(@Valid UserLoginRequestDto requestDto) {
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
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .accessToken(accessToken)
                .build();
    }

    // 회원 프로필 사진 수정
    public UserUpdateProfileImageResponseDto updateProfileImage(@Valid UserUpdateProfileImageRequestDto requestDto) {

        User user = jwtUtil.verifyUser();
        if(requestDto.getImageUrl() != null) {
            user.setImageUrl(requestDto.getImageUrl());
            userRepository.save(user);
        }

        return UserUpdateProfileImageResponseDto.builder()
                .imageUrl(user.getImageUrl())
                .build();
    }

    //
    public UserUpdateNicknameResponseDto updateNickname(@Valid UserUpdateNicknameRequestDto requestDto) {

        User user = jwtUtil.verifyUser();
        if(requestDto.getNickname() != null) {
            user.setNickname(requestDto.getNickname());
            userRepository.save(user);
        }
        return UserUpdateNicknameResponseDto.builder()
                .nickname(user.getNickname())
                .build();
    }

    // 비밀번호
    public User updatePassword(@Valid UserUpdatePasswordRequestDto requestDto) {

        User user = jwtUtil.verifyUser();
        if(requestDto.getPassword() != null) {
            String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
        }
        return user;
    }

    // 로그아웃
    public User logoutUser() {
        User user = jwtUtil.verifyUser();

        if(user.getRefreshToken() != null) {
            user.getRefreshToken().expireToken();
            refreshTokenRepository.save(user.getRefreshToken());
        }

        user.setRefreshToken(null);
        userRepository.save(user);
        return user;
    }

    // 회원 탈퇴
    public User unregisterUser() {
        User user = jwtUtil.verifyUser();
        Long userId = user.getId();

        long expectedCommentCount = commentRepository.countByUserIdAndDeletedFalse(userId);
        long expectedPostCount = postRepository.countByUserIdAndDeletedFalse(userId);

        try {
            int deletedComments = commentRepository.softDeletedCommentsByUserId(userId);
            int deletedPosts = postRepository.softDeletedPostsByUserId(userId);

            if(deletedComments != expectedCommentCount) {
                throw new RuntimeException("댓글 삭제 처리 과정에서 오류가 발생하였습니다.");
            }
            if(deletedPosts != expectedPostCount) {
                throw new RuntimeException("게시글 삭제 처리 과정에서 오류가 발생하였습니다.");
            }
            user.setDeleted(true);
            userRepository.save(user);
            return user;
        } catch (Exception e) {
            throw new RuntimeException("회원 탈퇴 처리 중 예외가 발생하였습니다.", e);
        }
    }
}
