package com.example.community.dto.Post.Response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikePostResponseDto {
    private long likeCount;
}
