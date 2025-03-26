package com.example.community.service.UserServiceTest;

import com.example.community.dto.User.Request.UserRegisterRequestDto;
import com.example.community.entity.User;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UnRegisterUserTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("회원 탈퇴 성공 - 반환 없음")
    void should_ReturnVoid_When_RegisterUserSuccess() {
        User mockUser = User.builder()
                .id(1L)
                .email("example@email.com")
                .password("encodedPassword")
                .nickname("지은")
                .build();

        when(jwtUtil.verifyUser()).thenReturn(mockUser);
        when(commentRepository.countByUserIdAndDeletedFalse(1L)).thenReturn(5);
        when(postRepository.countByUserIdAndDeletedFalse(1L)).thenReturn(5);
        when(commentRepository.softDeletedCommentsByUserId(1L)).thenReturn(5);
        when(postRepository.softDeletedPostsByUserId(1L)).thenReturn(5);

        userService.unregisterUser();

        verify(commentRepository, times(1)).countByUserIdAndDeletedFalse(1L);
        verify(commentRepository, times(1)).softDeletedCommentsByUserId(1L);
        verify(postRepository, times(1)).countByUserIdAndDeletedFalse(1L);
        verify(postRepository, times(1)).softDeletedPostsByUserId(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 댓글 삭제 오류")
    void should_ReturnException_When_UnMathched_CommentCount() {
        User mockUser = User.builder()
                .id(1L)
                .email("example@email.com")
                .password("encodedPassword")
                .nickname("지은")
                .build();

        when(jwtUtil.verifyUser()).thenReturn(mockUser);
        when(commentRepository.countByUserIdAndDeletedFalse(1L)).thenReturn(5);
        when(postRepository.countByUserIdAndDeletedFalse(1L)).thenReturn(5);
        when(commentRepository.softDeletedCommentsByUserId(1L)).thenReturn(3);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.unregisterUser();
        });

        assertEquals("댓글 삭제 처리 과정에서 오류가 발생하였습니다.", exception.getMessage());
        verify(commentRepository, times(1)).countByUserIdAndDeletedFalse(1L);
        verify(commentRepository, times(1)).softDeletedCommentsByUserId(1L);
        verify(postRepository, times(1)).countByUserIdAndDeletedFalse(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 게시글 삭제 오류")
    void should_ReturnException_When_UnMathched_PostCount() {
        User mockUser = User.builder()
                .id(1L)
                .email("example@email.com")
                .password("encodedPassword")
                .nickname("지은")
                .build();

        when(jwtUtil.verifyUser()).thenReturn(mockUser);
        when(commentRepository.countByUserIdAndDeletedFalse(1L)).thenReturn(5);
        when(postRepository.countByUserIdAndDeletedFalse(1L)).thenReturn(5);
        when(commentRepository.softDeletedCommentsByUserId(1L)).thenReturn(5);
        when(postRepository.softDeletedPostsByUserId(1L)).thenReturn(3);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.unregisterUser();
        });

        assertEquals("게시글 삭제 처리 과정에서 오류가 발생하였습니다.", exception.getMessage());
        verify(commentRepository, times(1)).countByUserIdAndDeletedFalse(1L);
        verify(commentRepository, times(1)).softDeletedCommentsByUserId(1L);
        verify(postRepository, times(1)).countByUserIdAndDeletedFalse(1L);
        verify(postRepository, times(1)).softDeletedPostsByUserId(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}
