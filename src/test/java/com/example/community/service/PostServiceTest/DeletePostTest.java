package com.example.community.service.PostServiceTest;

import com.example.community.dto.Post.Request.PostUpdateRequestDto;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.repository.PostRepository;
import com.example.community.security.JwtUtil;
import com.example.community.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class DeletePostTest {
    @Mock
    private PostRepository postRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private PostService postService;

    @Test
    @DisplayName("게시글 삭제 성공 - 수정 권한 있음")
    void should_ReturnVoid_When_DeletePost_Success() {
        User author = User.builder()
                .id(1L)
                .build();

        Post post = Post.builder()
                .id(2L)
                .user(author)
                .title("title")
                .content("content")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        User currentUser = User.builder()
                .id(1L)
                .build();


        when(jwtUtil.verifyUser()).thenReturn(currentUser);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        postService.deletePost(2L);

        verify(postRepository, times(1)).findById(2L);
        verify(postRepository, times(1)).save(post);
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 게시글 존재하지 않음")
    void should_ReturnError_When_PostNotFOund() {
        User author = User.builder()
                .id(1L)
                .build();

        User currentUser = User.builder()
                .id(2L)
                .build();


        when(jwtUtil.verifyUser()).thenReturn(currentUser);
        when(postRepository.findById(2L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            postService.deletePost(2L);
        });

        assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());
        verify(postRepository, times(1)).findById(currentUser.getId());
    }

    @Test
    @DisplayName("게시글 수정 실패 - 수정 권한 없음")
    void should_ReturnError_When_InvalidUser() {
        User author = User.builder()
                .id(1L)
                .build();

        Post post = Post.builder()
                .id(2L)
                .user(author)
                .title("title")
                .content("content")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        User currentUser = User.builder()
                .id(3L)
                .build();


        when(jwtUtil.verifyUser()).thenReturn(currentUser);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            postService.deletePost(2L);
        });

        assertEquals("게시글 삭제 권한이 없습니다.", exception.getMessage());
        verify(postRepository, times(1)).findById(post.getId());
        verify(postRepository, never()).save(post);
    }
}
