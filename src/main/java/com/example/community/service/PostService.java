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
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public PostService(UserRepository userRepository, PostRepository postRepository, LikeRepository likeRepository,
                       JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    // 게시글 생성
    public PostCreateResponseDto createPost(PostCreateRequestDto requestDto, HttpServletRequest request) {
        User user = jwtUtil.verifyUser(request);

        if(StringUtils.hasText(requestDto.getTitle()) && StringUtils.hasText(requestDto.getContent())) {
            if (requestDto.getImageUrl() != null) {
                 Post post = Post.builder()
                        .title(requestDto.getTitle())
                        .content(requestDto.getContent())
                        .imageUrl(requestDto.getImageUrl())
                        .user(user)
                        .build();
                 postRepository.save(post);

                return PostCreateResponseDto.builder()
                        .id(post.getId())
                        .build();
            } else {
                Post post = Post.builder()
                        .title(requestDto.getTitle())
                        .content(requestDto.getContent())
                        .user(user)
                        .build();
                postRepository.save(post);

                return PostCreateResponseDto.builder()
                        .id(post.getId())
                        .build();
            }
        } else {
            throw new IllegalArgumentException("글의 제목과 본문 내용을 입력해주세요.");
        }
    }

    // 게시글 수정
    public Post updatePost(Long postId, PostUpdateRequestDto requestDto, HttpServletRequest request) {
        User user = jwtUtil.verifyUser(request);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 프론트에서도 예외 처리 필요
        if (!post.getUser().getId().equals(user.getId())) {
            throw new SecurityException("게시글을 수정할 권한이 없습니다.");
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
                        .comments(post.getComments()
                                .stream()
                                .filter(comment -> !comment.isDeleted())
                                .map(comment -> CommentResponseDto.builder()
                                        .id(comment.getId())
                                        .content(comment.getContent())
                                        .user(UserResponseDto.builder()  // 🔥 댓글 작성자 정보 추가
                                                .id(comment.getUser().getId())
                                                .nickname(comment.getUser().getNickname())
                                                .imageUrl(comment.getUser().getImageUrl())
                                                .build())
                                        .createdAt(comment.getCreatedAt())
                                        .build())
                                .collect(Collectors.toList()))
                        .createdAt(post.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // 게시글 상세 조회
    public PostDetailResponseDto readPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
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
                .build();
    }

    // 게시글 삭제
    public Post deletePost(Long postId, HttpServletRequest request) {
        User user = jwtUtil.verifyUser(request);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));

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
    public LikePostResponseDto likePost(Long postId, HttpServletRequest request) {
        User user = jwtUtil.verifyUser(request);

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
