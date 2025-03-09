package com.example.community.dto.Post.Response;

import com.example.community.entity.Comment;
import com.example.community.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@SuperBuilder
public class PostDetailResponseDto extends AbstractPostResponseDto {
    private String content;
    private String imageUrl;

    public PostDetailResponseDto(Long id, String title, String content, String imageUrl, int likes, int views, LocalDateTime createdAt, User user, List<Comment> comments) {
        super(id, title, likes, views, user, comments, createdAt);
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
