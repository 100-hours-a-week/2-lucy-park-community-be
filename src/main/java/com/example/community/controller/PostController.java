package com.example.community.controller;

import com.example.community.dto.Post.Request.PostCreateRequestDto;
import com.example.community.dto.Post.Request.PostUpdateRequestDto;
import com.example.community.dto.Post.Response.LikePostResponseDto;
import com.example.community.dto.Post.Response.PostCreateResponseDto;
import com.example.community.dto.Post.Response.PostDetailResponseDto;
import com.example.community.dto.Post.Response.PostListResponseDto;
import com.example.community.dto.Wrapper.WrapperResponse;
import com.example.community.dto.Wrapper.WrapperWithoutDataResponse;
import com.example.community.entity.Post;
import com.example.community.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<WrapperResponse> readPosts() {
        List<PostListResponseDto> posts = postService.readPosts();
        WrapperResponse response = new WrapperResponse("get_posts_success", posts);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<WrapperResponse> createPost(@Valid @RequestBody PostCreateRequestDto requestDto) {
        PostCreateResponseDto responseDto = postService.createPost(requestDto);
        WrapperResponse response = new WrapperResponse("create_post_success", responseDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<WrapperWithoutDataResponse> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequestDto requestDto) {
        Post post = postService.updatePost(postId, requestDto);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("update_post_success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<WrapperResponse> readPost(@PathVariable Long postId) {
        PostDetailResponseDto responseDto = postService.readPost(postId);
        WrapperResponse response = new WrapperResponse("read_post_success", responseDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<WrapperWithoutDataResponse> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("delete_post_success");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<WrapperResponse> likePost(@PathVariable Long postId) {
        LikePostResponseDto responseDto = postService.likePost(postId);
        WrapperResponse response = new WrapperResponse<>("like_post_success", responseDto);
        return ResponseEntity.ok(response);
    }
}
