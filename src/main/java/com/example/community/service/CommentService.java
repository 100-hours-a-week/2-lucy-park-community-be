package com.example.community.service;

import com.example.community.dto.Comment.Request.CommentCreateRequestDto;
import com.example.community.dto.Comment.Request.CommentUpdateRequestDto;
import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.RefreshTokenRepository;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository.findAllByPostIdAndDeletedFalseOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> CommentResponseDto.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .user(UserResponseDto.builder()
                                .id(comment.getUser().getId())
                                .nickname(comment.getUser().getNickname())
                                .imageUrl(comment.getUser().getImageUrl())
                                .build())
                        .createdAt(comment.getCreatedAt())
                        .build()
                )
                .collect(Collectors.toList())
                ;

    }

    public Comment createComment(Long postId, @Valid CommentCreateRequestDto requestDto) {
        User user = jwtUtil.verifyUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Comment comment = Comment.builder()
                .post(post)
                .content(requestDto.getContent())
                .user(user)
                .build();

        commentRepository.save(comment);
        return comment;
    }

    public Comment updateComment(Long postId, Long commentId, @Valid CommentUpdateRequestDto requestDto) {
        User user = jwtUtil.verifyUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        Comment comment = commentRepository.findCommentByIdAndPostId(commentId, post.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if(!comment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("댓글 수정 권한이 없습니다.");
        }

        comment.setContent(requestDto.getContent());
        commentRepository.save(comment);
        return comment;
    }

    public Comment deleteComment(Long postId, Long commentId) {
        User user = jwtUtil.verifyUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));


        Comment comment = commentRepository.findCommentByIdAndPostId(commentId, post.getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다."));

        if(!comment.getUser().getId().equals(user.getId())) {
            throw new SecurityException("댓글 삭제 권한이 없습니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
        return comment;
    }
}
