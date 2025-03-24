package com.example.community.controller.PostControllerTest;


import com.example.community.controller.PostController;
import com.example.community.dto.Post.Request.PostCreateRequestDto;
import com.example.community.dto.Post.Request.PostUpdateRequestDto;
import com.example.community.dto.User.Request.UserRegisterRequestDto;
import com.example.community.security.JwtTokenProvider;
import com.example.community.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UpdatePostTest {
    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 수정 성공 - 성공적으로 200 반환")
    void updatePost_success() throws Exception {
        PostUpdateRequestDto requestDto = PostUpdateRequestDto.builder()
                .title("example")
                .content("example content")
                .imageUrl("/uploads/thumbnail_another_image.jpg")
                .build();

        mockMvc.perform(patch("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("update_post_success"));

        verify(postService).updatePost(eq(1L), refEq(requestDto));
    }

    @Test
    @DisplayName("게시글 작성 실패 - 이미지 형식이 잘못됨")
    void updatePost_invalidImageUrl() throws Exception {
        PostUpdateRequestDto requestDto = PostUpdateRequestDto.builder()
                .title("example")
                .content("example content")
                .imageUrl("invalid_image")
                .build();

        mockMvc.perform(patch("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(postService, never()).updatePost(eq(1L), refEq(requestDto));
    }
}
