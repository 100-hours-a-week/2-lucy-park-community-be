package com.example.community.dto.Post.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequestDto {
    @NotBlank
    private Long postId;

    private String title;
    private String content;
    private String imageUrl;
}
