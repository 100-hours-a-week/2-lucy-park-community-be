package com.example.community.dto.Comment.Request;

import com.example.community.dto.Auth.AuthenticatedRequestDto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequestDto extends AuthenticatedRequestDto {
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}
