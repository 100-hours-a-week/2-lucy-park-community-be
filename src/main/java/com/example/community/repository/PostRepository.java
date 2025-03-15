package com.example.community.repository;

import com.example.community.entity.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findById(Long id);

    @EntityGraph(attributePaths = {"comments"})
    @Query("SELECT p FROM Post p WHERE p.deleted = false")
    List<Post> findAllWithComments();
}
