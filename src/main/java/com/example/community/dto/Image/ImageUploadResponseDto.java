package com.example.community.dto.Image;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageUploadResponseDto {
    private String message;
    private String imageUrl;
}
