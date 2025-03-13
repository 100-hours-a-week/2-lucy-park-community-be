package com.example.community.controller;

import com.example.community.dto.Post.Request.PostCreateRequestDto;
import com.example.community.dto.Post.Request.PostUpdateRequestDto;
import com.example.community.dto.Post.Response.PostListResponseDto;
import com.example.community.entity.Post;
import com.example.community.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<List<PostListResponseDto>> readPosts() {
        return ResponseEntity.ok(postService.readPosts());
    }

    @PostMapping
    public ResponseEntity<String> createPost(@Valid @RequestBody PostCreateRequestDto requestDto, HttpServletRequest request) {
        Post post = postService.createPost(requestDto, request);
        return ResponseEntity.ok("게시글 작성에 성공하였습니다.");
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<String> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequestDto requestDto, HttpServletRequest request) {
        Post post = postService.updatePost(postId, requestDto, request);
        return ResponseEntity.ok("게시글 수정에 성공하였습니다.");
    }

}
