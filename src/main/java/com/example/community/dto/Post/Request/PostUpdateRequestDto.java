package com.example.community.dto.Post.Request;

import com.example.community.dto.Auth.AuthenticatedRequestDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequestDto extends AuthenticatedRequestDto {
    @NotBlank
    private Long postId;

    private String title;
    private String content;
    private String imageUrl;
}
