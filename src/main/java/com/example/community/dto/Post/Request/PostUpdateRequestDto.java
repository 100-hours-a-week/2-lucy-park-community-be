package com.example.community.dto.Post.Request;

import com.example.community.annotation.ContainsThumbnailPath;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequestDto {
    private String title;
    private String content;

    @ContainsThumbnailPath
    private String imageUrl;
}
