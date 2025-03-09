package com.example.community.dto.Post.Response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostUpdateResponseDto {
    private final Long id;
    private final String title;
    private final String content;
    private final String imageUrl;
    private final int likes;
    private final int views;
}
