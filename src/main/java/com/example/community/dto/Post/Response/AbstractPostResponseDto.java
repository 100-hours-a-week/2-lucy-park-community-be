package com.example.community.dto.Post.Response;

import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AbstractPostResponseDto {
    protected Long id;
    protected String title;
    protected int likes;
    protected int views;
    protected UserResponseDto user;

    @Builder.Default
    protected List<CommentResponseDto> comments = new ArrayList<>();

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    protected LocalDateTime createdAt;
}
