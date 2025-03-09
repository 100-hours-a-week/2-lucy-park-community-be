package com.example.community.controller;

import com.example.community.dto.ImageUploadResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@Slf4j
public class ImageController {

    private static final String UPLOAD_DIR = "/uploads/";

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("imageFile") MultipartFile imageFile,
                                         @RequestParam("type") String type) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ImageUploadResponseDto("upload_fail_client", "Image file is required"));
        }

        try {
            String originalFilename = StringUtils.cleanPath(imageFile.getOriginalFilename());
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String storedFilename = "thumbnail_" + UUID.randomUUID() + extension;

            // 저장 경로를 /uploads 디렉토리로 설정
            Path uploadPath = Path.of("src/main/resources/static/uploads").toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 파일 저장
            Path filePath = uploadPath.resolve(storedFilename);
            Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 실제 클라이언트가 접근 가능한 URL 반환 (localhost 기준)
            String imageUrl = "/uploads/" + storedFilename;
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ImageUploadResponseDto("upload_success", imageUrl));

        } catch (IOException e) {
            log.error("Image upload failed", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ImageUploadResponseDto("upload_fail_server", null));
        }
    }
}
