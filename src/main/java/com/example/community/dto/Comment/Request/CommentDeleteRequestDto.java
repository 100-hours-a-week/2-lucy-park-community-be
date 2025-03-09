package com.example.community.dto.Comment.Request;

import com.example.community.dto.Auth.AuthenticatedRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDeleteRequestDto extends AuthenticatedRequestDto {
    private Long commentId;
}
