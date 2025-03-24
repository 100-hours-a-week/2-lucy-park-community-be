package com.example.community.repository;

import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);

    @EntityGraph(attributePaths = {"comments"})
    @Query("SELECT p FROM Post p WHERE p.deleted = false")
    List<Post> findAllWithComments();

    @Modifying
    @Query("UPDATE Post p SET p.deleted = true where p.user.id = :userId AND p.deleted = false ")
    int softDeletedPostsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.user.id = :userId AND p.deleted = false")
    int countByUserIdAndDeletedFalse(@Param("userId") Long userId);
}
