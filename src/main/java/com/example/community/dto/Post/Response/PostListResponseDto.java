package com.example.community.dto.Post.Response;

import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@SuperBuilder
public class PostListResponseDto extends AbstractPostResponseDto{
    private int commentCount;

    public PostListResponseDto(Long id, String title, int likeCount, int viewCount, UserResponseDto responseDto, LocalDateTime createdAt, int commentCount ) {
        super(id, title, likeCount, viewCount, responseDto, createdAt);
        this.commentCount = commentCount;
    }
}
