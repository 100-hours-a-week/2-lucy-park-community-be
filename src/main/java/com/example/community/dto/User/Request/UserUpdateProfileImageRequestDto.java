package com.example.community.dto.User.Request;

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
public class UserUpdateProfileImageRequestDto {
    @NotBlank(message = "이미지를 첨부해주세요.")
    @ContainsThumbnailPath
    private String imageUrl;
}
