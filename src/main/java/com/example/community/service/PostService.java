package com.example.community.service;

import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.Post.Request.PostCreateRequestDto;
import com.example.community.dto.Post.Request.PostUpdateRequestDto;
import com.example.community.dto.Post.Response.LikePostResponseDto;
import com.example.community.dto.Post.Response.PostCreateResponseDto;
import com.example.community.dto.Post.Response.PostDetailResponseDto;
import com.example.community.dto.Post.Response.PostListResponseDto;
import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Like;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import com.example.community.repository.LikeRepository;
import com.example.community.repository.PostRepository;
import com.example.community.repository.UserRepository;
import com.example.community.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentService commentService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // 게시글 생성
    public PostCreateResponseDto createPost(@Valid PostCreateRequestDto requestDto) {
        User user = jwtUtil.verifyUser();

        if(StringUtils.hasText(requestDto.getTitle()) && StringUtils.hasText(requestDto.getContent())) {

            Post.PostBuilder builder = Post.builder()
                    .title(requestDto.getTitle())
                    .content(requestDto.getContent())
                    .user(user);

            if (StringUtils.hasText(requestDto.getImageUrl())) {
                builder.imageUrl(requestDto.getImageUrl());
            }

            Post post = builder.build();

            postRepository.save(post);


            return PostCreateResponseDto.builder()
                    .id(post.getId())
                    .build();
        } else {
            throw new IllegalArgumentException("글의 제목과 본문 내용을 입력해주세요.");
        }
    }

    // 게시글 수정
    public Post updatePost(Long postId, @Valid PostUpdateRequestDto requestDto) {
        User user = jwtUtil.verifyUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 프론트에서도 예외 처리 필요
        if (!post.getUser().getId().equals(user.getId())) {
            throw new SecurityException("게시글 수정 권한이 없습니다.");
        }

        if(StringUtils.hasText(requestDto.getTitle())) {
            post.setTitle(requestDto.getTitle());
        }

        if (StringUtils.hasText(requestDto.getContent())) {
            post.setContent(requestDto.getContent());
        }

        if (requestDto.getImageUrl() != null) {
            post.setImageUrl(requestDto.getImageUrl());
        }

        postRepository.save(post);
        return post;
    }

    // 전체 게시글 조회
    @Transactional(readOnly = true)
    public List<PostListResponseDto> readPosts() {
        return postRepository.findAllWithComments().stream()
                .sorted(Comparator.comparing(Post::getId))
                .map(post -> PostListResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .likeCount(post.getLikesCount())
                        .viewCount(post.getViewsCount())
                        .user(UserResponseDto.builder()
                                .id(post.getUser().getId())
                                .nickname(post.getUser().getNickname())
                                .imageUrl(post.getUser().getImageUrl())
                                .build())
                        .commentCount(post.getComments().size())
                        .build())
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    public PostDetailResponseDto readPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));
        post.setViewsCount(post.getViewsCount() + 1);
        postRepository.save(post);

        return PostDetailResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .likeCount(post.getLikesCount())
                .viewCount(post.getViewsCount())
                .user(UserResponseDto.builder()
                        .id(post.getUser().getId())
                        .nickname(post.getUser().getNickname())
                        .imageUrl(post.getUser().getImageUrl())
                        .build())
                .createdAt(post.getCreatedAt())
                .comments(commentService.getComments(postId))
                .build();
    }

    // 게시글 삭제
    public Post deletePost(Long postId) {
        User user = jwtUtil.verifyUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 마찬가지로 프론트엔드에서도 처리 필요
        if(!post.getUser().getId().equals(user.getId())) {
            throw new SecurityException("게시글 삭제 권한이 없습니다.");
        }

        post.setDeleted(true);
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);

        return post;
    }

    // 게시글 좋아요 / 취소
    public LikePostResponseDto likePost(Long postId) {
        User user = jwtUtil.verifyUser();

        Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));

        // 좋아요 여부 찾아서 취소하기
        if(likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            Like like = likeRepository.findByPostIdAndUserId(postId, user.getId());

            post.setLikesCount(post.getLikesCount() - 1);
            postRepository.save(post);

            likeRepository.delete(like);
        } else {
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);

            Like like = Like.builder()
                    .user(user)
                    .post(post)
                    .build();
            likeRepository.save(like);
        }

        return LikePostResponseDto.builder()
                .likeCount(post.getLikesCount())
                .build();
    }

}
