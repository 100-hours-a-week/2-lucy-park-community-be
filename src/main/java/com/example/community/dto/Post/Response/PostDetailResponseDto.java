package com.example.community.dto.Post.Response;

import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@SuperBuilder
public class PostDetailResponseDto extends AbstractPostResponseDto {
    private String content;
    private String imageUrl;

    @Builder.Default
    protected List<CommentResponseDto> comments = new ArrayList<>();

    public PostDetailResponseDto(Long id, String title, String content, String imageUrl, int likeCount, int viewCount, int commentCount, LocalDateTime createdAt, UserResponseDto responseDto, List<CommentResponseDto> comments) {
        super(id, title, likeCount, viewCount, commentCount, responseDto, createdAt);
        this.content = content;
        this.imageUrl = imageUrl;
        this.comments = comments;
    }
}
