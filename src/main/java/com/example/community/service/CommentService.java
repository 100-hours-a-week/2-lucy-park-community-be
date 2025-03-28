package com.example.community.service;

import com.example.community.dto.Comment.Request.CommentCreateRequestDto;
import com.example.community.dto.Comment.Request.CommentUpdateRequestDto;
import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.Comment.Response.ParentCommentDto;
import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import com.example.community.mapper.CommentMapper;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
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

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long postId) {
        return commentRepository.findTopLevelCommentsWithChildrenOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentMapper::convertToDto)
                .collect(Collectors.toList());
    }

    public Comment createComment(Long postId, @Valid CommentCreateRequestDto requestDto) {
        User user = jwtUtil.verifyUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

        Comment.CommentBuilder commentBuilder = Comment.builder()
                .post(post)
                .content(requestDto.getContent())
                .user(user);

        // 부모 댓글 ID가 존재하면 대댓글로 처리
        if (requestDto.getParentCommentId() != null) {
            Comment parentComment = commentRepository.findById(requestDto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
            commentBuilder.parent(parentComment);
        }

        Comment comment = commentBuilder.build();
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
            throw new AccessDeniedException("댓글 삭제 권한이 없습니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
        return comment;
    }
}
