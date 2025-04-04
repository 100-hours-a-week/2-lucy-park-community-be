package com.example.community.integration;

import com.example.community.dto.Comment.Request.CommentCreateRequestDto;
import com.example.community.dto.Post.Request.PostCreateRequestDto;
import com.example.community.dto.User.Request.UserLoginRequestDto;
import com.example.community.dto.User.Request.UserRegisterRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 통합 테스트
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CommentIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 -> 로그인 -> 게시글 작성 -> 댓글 작성 -> 댓글 수정 통합 테스트[성공]")
    void commentIntegrationTest_register_login_createPost_createComment_updateComment_success() throws Exception {
        // 회원가입
        UserRegisterRequestDto registerRequestDto = UserRegisterRequestDto.builder()
                .email("test@email.com")
                .password("Password**")
                .nickname("example")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("register_success"));

        // 로그인
        UserLoginRequestDto loginRequestDto = UserLoginRequestDto.builder()
                .email("test@email.com")
                .password("Password**")
                .build();

        String loginResponse = mockMvc.perform(post("/users/session")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("login_success"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper
                .readTree(loginResponse)
                .get("data")
                .get("accessToken")
                .asText();

        // 게시글 작성
        PostCreateRequestDto postCreateRequestDto = PostCreateRequestDto.builder()
                .title("post title")
                .content("post content")
                .imageUrl("/uploads/thumbnail_post")
                .build();

        String postCreateResponse = mockMvc.perform(post("/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("create_post_success"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long postId = objectMapper
                .readTree(postCreateResponse)
                .get("data")
                .get("id")
                .asLong();

        // 댓글 작성
        CommentCreateRequestDto commentCreateRequestDto = CommentCreateRequestDto.builder()
                .content("comment content")
                .build();

        mockMvc.perform(post("/posts/" + postId +"/comments")
                                .header("Authorization", "Bearer " + accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentCreateRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("add_comment_success"));
    }
}
