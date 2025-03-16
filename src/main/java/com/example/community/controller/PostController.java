package com.example.community.controller;

import com.example.community.dto.Post.Request.PostCreateRequestDto;
import com.example.community.dto.Post.Request.PostUpdateRequestDto;
import com.example.community.dto.Post.Response.PostDetailResponseDto;
import com.example.community.dto.Post.Response.PostListResponseDto;
import com.example.community.dto.User.Request.UserSigninRequestDto;
import com.example.community.dto.Wrapper.WrapperResponse;
import com.example.community.dto.Wrapper.WrapperWithoutDataResponse;
import com.example.community.entity.Post;
import com.example.community.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<WrapperResponse> readPosts() {
        List<PostListResponseDto> posts = postService.readPosts();
        WrapperResponse response = new WrapperResponse("get_posts_success", posts);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createPost(@Valid @RequestBody PostCreateRequestDto requestDto, HttpServletRequest request) {
        Post post = postService.createPost(requestDto, request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "게시글 작성에 성공하였습니다.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<WrapperWithoutDataResponse> updatePost(@PathVariable Long postId, @Valid @RequestBody PostUpdateRequestDto requestDto, HttpServletRequest request) {
        Post post = postService.updatePost(postId, requestDto, request);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("게시글 수정에 성공하였습니다.");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<WrapperResponse> readPost(@PathVariable Long postId) {
        PostDetailResponseDto responseDto = postService.readPost(postId);
        WrapperResponse response = new WrapperResponse("read_post_success", responseDto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<WrapperWithoutDataResponse> deletePost(@PathVariable Long postId,
                                             HttpServletRequest request) {
        postService.deletePost(postId, request);
        WrapperWithoutDataResponse response = new WrapperWithoutDataResponse("게시글 삭제에 성공하였습니다.");
        return ResponseEntity.ok(response);
    }
}
