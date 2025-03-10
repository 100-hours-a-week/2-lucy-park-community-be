package com.example.community.repository;

import com.example.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findCommentById(Long id);

    List<Comment> findByPostId(Long id);
}
