package com.example.community.dto.Comment.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequestDto {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
