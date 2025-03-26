package com.example.community.service.UserServiceTest;

import com.example.community.dto.User.Request.UserUpdateNicknameRequestDto;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateNicknameTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("프로필 닉네임 변경 성공 - 중복되지 않은 닉네임")
    void should_ReturnNickname_When_UpdateNicknameSuccess() {
        User mockUser = User.builder()
                .email("example@email.com")
                .password("encodedPassword")
                .nickname("지은")
                .build();

        UserUpdateNicknameRequestDto requestDto = UserUpdateNicknameRequestDto.builder()
                .nickname("example")
                .build();

        when(jwtUtil.verifyUser()).thenReturn(mockUser);
        when(userRepository.findByNickname("example")).thenReturn(Optional.empty());

        userService.updateNickname(requestDto);

        verify(userRepository).findByNickname("example");
        verify(userRepository).save(mockUser);
    }

    @Test
    @DisplayName("프로필 닉네임 변경 실패 - 중복된 닉네임")
    void should_ReturnNickname_When_DuplicatedNickname() {
        User mockUser = User.builder()
                .email("example@email.com")
                .password("encodedPassword")
                .nickname("example")
                .build();

        UserUpdateNicknameRequestDto requestDto = UserUpdateNicknameRequestDto.builder()
                .nickname("example")
                .build();

        when(jwtUtil.verifyUser()).thenReturn(mockUser);
        when(userRepository.findByNickname("example")).thenReturn(Optional.of(mockUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateNickname(requestDto);
        });

        assertEquals("이미 사용 중인 닉네임입니다.", exception.getMessage());

        verify(userRepository).findByNickname("example");
        verify(userRepository, never()).save(mockUser);
    }
}
