package com.example.community.repository;

import com.example.community.entity.Comment;
import com.example.community.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPostIdOrderByCreatedAtAsc(Long postId);
    Optional<Comment> findCommentByIdAndPostId(Long id, Long postId);

    @Modifying
    @Query("UPDATE Comment c SET c.deleted = true WHERE c.user.id = :userId AND c.deleted = false")
    int softDeletedCommentsByUserId(@Param("userId") Long userId);

    @Query("SELECT Count(c) FROM Comment c WHERE c.user.id = :userId AND c.deleted = false")
    int countByUserIdAndDeletedFalse(@Param("userId") Long userId);
}
