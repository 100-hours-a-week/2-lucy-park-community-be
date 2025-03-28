package com.example.community.mapper;

import com.example.community.dto.Comment.Response.CommentResponseDto;
import com.example.community.dto.Comment.Response.ParentCommentDto;
import com.example.community.dto.User.Response.UserResponseDto;
import com.example.community.entity.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommentMapper {

    public static CommentResponseDto convertToDto(Comment comment) {
        // 최상위 댓글의 경우, parent는 null이며, children은 flattenDescendants()를 통해 평탄화된 자식 댓글 목록을 설정합니다.
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .user(UserResponseDto.builder()
                        .id(comment.getUser().getId())
                        .nickname(comment.isDeleted() ? "(알 수 없음)" : comment.getUser().getNickname())
                        .imageUrl(comment.isDeleted() ? "/uploads/thumbnail_basic_image.png" : comment.getUser().getImageUrl())
                        .build())
                .createdAt(comment.getCreatedAt())
                .parent(null)
                .children(flattenDescendants(comment))
                .build();
    }

    /**
     * flattenDescendants() 메서드는 주어진 댓글의 모든 하위 댓글(자식, 자식의 자식 등)을 평탄하게 수집하여,
     * 각 자식 댓글은 convertToDtoFlat()을 통해 변환한 후 하나의 리스트로 반환합니다.
     */
    private List<CommentResponseDto> flattenDescendants(Comment comment) {
        List<CommentResponseDto> flatList = new ArrayList<>();
        if (comment.getChildren() != null) {
            for (Comment child : comment.getChildren()) {
                // 자식 댓글은 convertToDtoFlat()을 통해 변환 (이 때, children은 빈 리스트로 설정)
                flatList.add(convertToDtoFlat(child));
                // 자식 댓글의 모든 후손도 평탄하게 추가
                flatList.addAll(flattenDescendants(child));
            }
        }
        return flatList;
    }

    /**
     * convertToDtoFlat() 메서드는 최상위가 아닌 대댓글(어떤 depth이든 상관없이)을 평탄하게 변환합니다.
     * 여기서는 children 필드를 빈 리스트로 처리하여, 재귀적 중첩 없이 단일 레벨로 표현합니다.
     */
    private CommentResponseDto convertToDtoFlat(Comment comment) {
        ParentCommentDto parentDto = null;
        if (comment.getParent() != null) {
            parentDto = ParentCommentDto.builder()
                    .id(comment.getParent().getId())
                    .build();
        }
        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.isDeleted() ? "삭제된 댓글입니다." : comment.getContent())
                .user(UserResponseDto.builder()
                        .id(comment.getUser().getId())
                        .nickname(comment.isDeleted() ? "(알 수 없음)" : comment.getUser().getNickname())
                        .imageUrl(comment.isDeleted() ? "/uploads/thumbnail_basic_image.png" : comment.getUser().getImageUrl())
                        .build())
                .createdAt(comment.getCreatedAt())
                .parent(parentDto)
                .children(Collections.emptyList()) // 평탄화된 자식 댓글은 재귀적 children 없이 빈 리스트로 처리
                .build();
    }
}
