package com.example.community.dto.Post.Response;

import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Comment;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
public class PostListResponseDto extends AbstractPostResponseDto{
    public PostListResponseDto(Long id, String title, int likes, int views, UserResponseDto responseDto, LocalDateTime createdAt, List<CommentResponseDto> comments) {
        super(id, title, likes, views, responseDto, comments, createdAt);
    }
}
