package com.example.community.controller;


import com.example.community.dto.Comment.Request.CommentCreateRequestDto;
import com.example.community.dto.Comment.Request.CommentUpdateRequestDto;
import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.Wrapper.WrapperResponse;
import com.example.community.dto.Wrapper.WrapperWithoutDataResponse;
import com.example.community.entity.Comment;
import com.example.community.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<WrapperResponse> readComments(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getComments(postId);
        WrapperResponse response = new WrapperResponse("read_comments_success", comments);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WrapperWithoutDataResponse> createComment(@PathVariable Long postId,
                                               @Valid @RequestBody CommentCreateRequestDto requestDto, HttpServletRequest request) {
        Comment comment = commentService.createComment(postId, requestDto, request);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("add_comment_success");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<WrapperWithoutDataResponse> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @Valid @RequestBody CommentUpdateRequestDto requestDto, HttpServletRequest request) {
        Comment comment = commentService.updateComment(postId, commentId, requestDto, request);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("edit_comment_success");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<WrapperWithoutDataResponse> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                HttpServletRequest request) {
        Comment comment = commentService.deleteComment(postId, commentId, request);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("delete_comment_success");
        return ResponseEntity.ok(response);
    }
}
