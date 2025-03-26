package com.example.community.controller.PostControllerTest;

import com.example.community.controller.PostController;
import com.example.community.dto.Post.Request.PostCreateRequestDto;
import com.example.community.dto.Post.Response.PostCreateResponseDto;
import com.example.community.dto.User.Request.UserRegisterRequestDto;
import com.example.community.dto.User.Response.UserUpdateProfileImageResponseDto;
import com.example.community.entity.Post;
import com.example.community.security.JwtTokenProvider;
import com.example.community.service.PostService;
import com.example.community.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CreatePostTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private PostService postService;

    @SuppressWarnings("removal")
    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 작성 성공 - 성공적으로 201 반환")
    void createPost_success() throws Exception {
        PostCreateRequestDto requestDto = PostCreateRequestDto.builder()
                        .title("example")
                        .content("example content")
                        .imageUrl("/uploads/thumbnail_another_image.jpg")
                        .build();

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("create_post_success"));

        verify(postService, times(1)).createPost(any());
    }

    @Test
    @DisplayName("게시글 작성 실패 - 제목 형식 오류")
    void createPost_invalidTitle() throws Exception {
        PostCreateRequestDto requestDto = PostCreateRequestDto.builder()
                .title("")
                .content("example content")
                .imageUrl("/uploads/thumbnail_another_image.jpg")
                .build();

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(postService, never()).createPost(any());
    }

    @Test
    @DisplayName("게시글 작성 실패 - 내용 형식이 잘못됨")
    void createPost_invalidNickname() throws Exception {
        PostCreateRequestDto requestDto = PostCreateRequestDto.builder()
                .title("example")
                .content("")
                .imageUrl("/uploads/thumbnail_another_image.jpg")
                .build();

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(postService, never()).createPost(any());
    }

    @Test
    @DisplayName("게시글 작성 실패 - 이미지 형식이 잘못됨")
    void createPost_invalidImageUrl() throws Exception {
        PostCreateRequestDto requestDto = PostCreateRequestDto.builder()
                .title("example")
                .content("")
                .imageUrl("invalid_image")
                .build();

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());

        verify(postService, never()).createPost(any());
    }

    @Test
    @DisplayName("게시글 작성 실패 - JSON 형식 아님")
    void createPost_notJson() throws Exception {
        String invalidContent = "title=example";

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(invalidContent))
                .andExpect(status().isBadRequest());

        verify(postService, never()).createPost(any());
    }

    @Test
    @DisplayName("게시글 작성 실패 - 잘못된 필드명")
    void createPost_invalidFieldName() throws Exception {
        String requestWithWrongField = """
        {
            "title": "example,
            "content": "example content",
            "imageUrl": "/uploads/thumbnail_another_image.jpg",
        }
        """;

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestWithWrongField))
                .andExpect(status().isBadRequest());

        verify(postService, never()).createPost(any());
    }

    @Test
    @DisplayName("게시글 작성 실패 - 빈 JSON 객체")
    void createPost_emptyJson() throws Exception {
        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(postService, never()).createPost(any());
    }
}
