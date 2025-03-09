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
public class PostCreateRequestDto extends AuthenticatedRequestDto {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @NotBlank(message = "이미지를 첨부해주세요.")
    private String imageUrl;
}
