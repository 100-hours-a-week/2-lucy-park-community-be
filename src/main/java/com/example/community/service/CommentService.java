package com.example.community.service;

import com.example.community.dto.Comment.Request.CommentCreateRequestDto;
import com.example.community.dto.Comment.Request.CommentUpdateRequestDto;
import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.repository.CommentRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.RefreshTokenRepository;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final JwtUtil jwtUtil;

    public CommentService(UserRepository userRepository, PostRepository postRepository,
                          CommentRepository commentRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
        this.jwtUtil = jwtUtil;
    }

    public Comment createComment(Long postId, CommentCreateRequestDto requestDto, HttpServletRequest request) {
        User user = jwtUtil.verifyUser(request);
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

    public Comment updateComment(Long postId, Long commentId, CommentUpdateRequestDto requestDto, HttpServletRequest request) {
        User user = jwtUtil.verifyUser(request);

        Comment comment = commentRepository.findCommentByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if(!comment.getUser().getId().equals(user.getId())) {
            new SecurityException("댓글 수정 권한이 없습니다.");
        }

        comment.setContent(requestDto.getContent());
        commentRepository.save(comment);
        return comment;
    }

    public Comment deleteComment(Long postId, Long commentId, HttpServletRequest request) {
        User user = jwtUtil.verifyUser(request);

        Comment comment = commentRepository.findCommentByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

        if(!comment.getUser().getId().equals(user.getId())) {
            new SecurityException("댓글 삭제 권한이 없습니다.");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
        return comment;
    }
}
