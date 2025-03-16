package com.example.community.repository;

import com.example.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdAndDeletedFalseOrderByCreatedAtAsc(Long postId);
    Optional<Comment> findCommentByIdAndPostId(Long id, Long postId);
}
