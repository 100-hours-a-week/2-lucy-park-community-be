package com.example.community.service.CommentServiceTest;

import com.example.community.dto.Comment.Request.CommentUpdateRequestDto;
import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import com.example.community.security.JwtUtil;
import com.example.community.service.CommentService;
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
public class DeleteCommentTest {
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentService commentService;

    @Test
    @DisplayName("댓글 삭제 성공 - 삭제 권한 있음")
    void should_ReturnVoid_When_DeleteComment_Success() {
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

        Comment comment = Comment.builder()
                .id(2L)
                .user(author)
                .content("content")
                .build();

        User currentUser = User.builder()
                .id(1L)
                .build();


        when(jwtUtil.verifyUser()).thenReturn(currentUser);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(commentRepository.findCommentByIdAndPostId(2L, 2L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(2L, 2L);

        verify(postRepository, times(1)).findById(2L);
        verify(commentRepository, times(1)).findCommentByIdAndPostId(comment.getId(), post.getId());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 게시글 존재하지 않음")
    void should_ReturnError_When_PostNotFOund() {
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

        Comment comment = Comment.builder()
                .id(2L)
                .user(author)
                .content("content")
                .build();

        User currentUser = User.builder()
                .id(1L)
                .build();

        when(jwtUtil.verifyUser()).thenReturn(currentUser);
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.deleteComment(1L, 2L);
        });

        assertEquals("존재하지 않는 게시글입니다.", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 댓글 존재하지 않음")
    void should_ReturnError_When_CommentNotFOund() {
        User author = User.builder()
                .id(1L)
                .build();

        Post post = Post.builder()
                .id(1L)
                .user(author)
                .title("title")
                .content("content")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        Comment comment = Comment.builder()
                .id(3L)
                .user(author)
                .content("content")
                .build();

        User currentUser = User.builder()
                .id(1L)
                .build();

        when(jwtUtil.verifyUser()).thenReturn(currentUser);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(commentRepository.findCommentByIdAndPostId(2L, post.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            commentService.deleteComment(post.getId(), 2L);
        });

        assertEquals("존재하지 않는 댓글입니다.", exception.getMessage());
        verify(postRepository, times(1)).findById(1L);
        verify(commentRepository, times(1)).findCommentByIdAndPostId(2L, post.getId());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 삭제 권한 없음")
    void should_ReturnError_When_InvalidUser() {
        User author = User.builder()
                .id(2L)
                .build();

        Post post = Post.builder()
                .id(2L)
                .user(author)
                .title("title")
                .content("content")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        Comment comment = Comment.builder()
                .id(2L)
                .user(author)
                .content("content")
                .build();

        User currentUser = User.builder()
                .id(1L)
                .build();

        when(jwtUtil.verifyUser()).thenReturn(currentUser);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(commentRepository.findCommentByIdAndPostId(2L, 2L)).thenReturn(Optional.of(comment));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            commentService.deleteComment(2L, 2L);
        });

        assertEquals("댓글 삭제 권한이 없습니다.", exception.getMessage());
        verify(postRepository, times(1)).findById(2L);
        verify(commentRepository, times(1)).findCommentByIdAndPostId(comment.getId(), post.getId());
        verify(commentRepository, never()).save(comment);
    }
}
