package com.example.community.service;

import com.example.community.dto.User.Request.UserSigninRequestDto;
import com.example.community.entity.User;
import com.example.community.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void should_ReturnUser_When_RegisterUserSuccess() {
        // Given
        when(userRepository.existsByEmail("test@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserSigninRequestDto requestDto = UserSigninRequestDto.builder()
                .email("test@email.com")
                .password("Password**")
                .nickname("example")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        // When
        User registeredUser = userService.registerUser(requestDto);

        // Then
        String encodedPassword = passwordEncoder.encode("Password**");

        assertNotNull(registeredUser);
        assertEquals("test@email.com", registeredUser.getEmail());
        assertEquals(encodedPassword, registeredUser.getPassword());
        assertEquals("example", registeredUser.getNickname());
        verify(userRepository, times(1)).existsByEmail("test@email.com");
        verify(userRepository, times(1)).save(any(User.class));
    }
}
