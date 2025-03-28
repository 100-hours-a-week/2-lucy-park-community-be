package com.example.community.dto.Comment.Response;

import com.example.community.dto.User.Response.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private UserResponseDto user;
    private LocalDateTime createdAt;
    private ParentCommentDto parent;
    private List<CommentResponseDto> children;
}
