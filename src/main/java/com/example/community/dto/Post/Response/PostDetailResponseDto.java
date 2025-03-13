package com.example.community.dto.Post.Response;

import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Comment;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
public class PostDetailResponseDto extends AbstractPostResponseDto {
    private String content;
    private String imageUrl;

    public PostDetailResponseDto(Long id, String title, String content, String imageUrl, int likes, int views, LocalDateTime createdAt, UserResponseDto responseDto, List<Comment> comments) {
        super(id, title, likes, views, responseDto, comments, createdAt);
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
