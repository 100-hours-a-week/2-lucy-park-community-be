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
import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<WrapperResponse> readComments(@PathVariable Long postId) {
        List<CommentResponseDto> comments = commentService.getComments(postId);
        WrapperResponse response = new WrapperResponse("read_comments_success", comments);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WrapperWithoutDataResponse> createComment(@PathVariable Long postId,
                                               @Valid @RequestBody CommentCreateRequestDto requestDto) {
        Comment comment = commentService.createComment(postId, requestDto);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("add_comment_success");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<WrapperWithoutDataResponse> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId,
                                                @Valid @RequestBody CommentUpdateRequestDto requestDto) {
        Comment comment = commentService.updateComment(postId, commentId, requestDto);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("edit_comment_success");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<WrapperWithoutDataResponse> updateComment(@PathVariable Long postId,
                                                @PathVariable Long commentId) {
        Comment comment = commentService.deleteComment(postId, commentId);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("delete_comment_success");
        return ResponseEntity.ok(response);
    }
}
