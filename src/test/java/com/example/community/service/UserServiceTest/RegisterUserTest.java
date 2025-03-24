package com.example.community.service.UserServiceTest;

import com.example.community.dto.User.Request.UserRegisterRequestDto;
import com.example.community.entity.User;
import com.example.community.repository.UserRepository;
import com.example.community.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegisterUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 가입 성공 - 유저 반환")
    void should_ReturnUser_When_RegisterUserSuccess() {
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(passwordEncoder.encode("Password**")).thenReturn("encoded_password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserRegisterRequestDto requestDto = UserRegisterRequestDto.builder()
                .email("test@email.com")
                .password("Password**")
                .nickname("example")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        User registeredUser = userService.registerUser(requestDto);

        assertNotNull(registeredUser);
        assertEquals("encoded_password", registeredUser.getPassword());
        verify(userRepository, times(1)).existsByEmail("test@email.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - 중복된 이메일")
    void should_ThrowError_When_DuplicatedEmail() {
        when(userRepository.existsByEmail("test@email.com")).thenReturn(true);

        UserRegisterRequestDto requestDto = UserRegisterRequestDto.builder()
                .email("test@email.com")
                .password("Password**")
                .nickname("example")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(requestDto);
        });

        assertEquals("이미 가입된 이메일입니다.", exception.getMessage());

        verify(userRepository, times(1)).existsByEmail("test@email.com");
        verify(userRepository, never()).save(any(User.class));
    }
}
