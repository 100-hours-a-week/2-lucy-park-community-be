package com.example.community.dto.Comment.Request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequestDto {
    @NotBlank
    private Long commentId;

    @NotBlank(message = "수정할 내용을 입력해주세요.")
    private String content;
}
