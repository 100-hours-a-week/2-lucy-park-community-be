package com.example.community.controller;


import com.example.community.dto.Comment.Request.CommentCreateRequestDto;
import com.example.community.dto.Comment.Request.CommentUpdateRequestDto;
import com.example.community.entity.Comment;
import com.example.community.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<String> createComment(@PathVariable Long postId,
                                               @Valid @RequestBody CommentCreateRequestDto requestDto, HttpServletRequest request) {
        Comment comment = commentService.createComment(postId, requestDto, request);
        return ResponseEntity.ok("댓글 작성을 성공하였습니다.");
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @Valid @RequestBody CommentUpdateRequestDto requestDto, HttpServletRequest request) {
        Comment comment = commentService.updateComment(postId, commentId, requestDto, request);
        return ResponseEntity.ok("댓글 수정을 성공하였습니다.");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                HttpServletRequest request) {
        Comment comment = commentService.deleteComment(postId, commentId, request);
        return ResponseEntity.ok("댓글 삭제를 성공하였습니다.");
    }
}
