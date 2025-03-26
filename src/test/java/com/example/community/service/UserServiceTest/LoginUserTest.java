package com.example.community.service.UserServiceTest;

import com.example.community.dto.User.Request.UserLoginRequestDto;
import com.example.community.dto.User.Response.UserLoginResponseDto;
import com.example.community.entity.User;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtUtil;
import com.example.community.service.TokenService;
import com.example.community.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("로그인 성공 - 등록된 사용자")
    void ReturnAccessToken_When_LoginSuccess() {
        User mockUser = User.builder()
                .email("example@email.com")
                .password("encodedPassword")
                .nickname("지은")
                .build();

        UserLoginRequestDto requestDto = UserLoginRequestDto.builder()
                .email("example@email.com")
                .password("rawPassword")
                .build();

        when(userRepository.findByEmail("example@email.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);
        when(tokenService.generateTokensForUser(mockUser)).thenReturn("mockAccessToken");

        UserLoginResponseDto responseDto = userService.loginUser(requestDto);

        assertEquals("mockAccessToken", responseDto.getAccessToken());
        verify(userRepository, times(1)).findByEmail(any());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void ThrowError_When_MismatchPassword() {

        UserLoginRequestDto requestDto = UserLoginRequestDto.builder()
                .email("example@email.com")
                .password("rawPassword")
                .build();

        when(userRepository.findByEmail("example@email.com")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.loginUser(requestDto);
        });

        assertEquals("존재하지 않는 이용자입니다.", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void ThrowError_When_InvaildUser() {
        User mockUser = User.builder()
                .email("example@email.com")
                .password("encodedPassword")
                .nickname("지은")
                .build();

        UserLoginRequestDto requestDto = UserLoginRequestDto.builder()
                .email("example@email.com")
                .password("rawPassword")
                .build();

        when(userRepository.findByEmail("example@email.com")).thenReturn(Optional.of(mockUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.loginUser(requestDto);
        });

        assertEquals("비밀번호가 일치하지 않습니다.", exception.getMessage());

        verify(userRepository, times(1)).findByEmail(any());
        verify(userRepository, never()).save(any(User.class));
    }
}
