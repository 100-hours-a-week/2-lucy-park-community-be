package com.example.community.repository;

import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@EntityScan(basePackages = "com.example.community.entity")
@ActiveProfiles("test")
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("삭제되지 않았던 댓글만 모두 삭제 처리")
    void should_ChangeStatusAllCommentDeleted_WhenNotDeleted() {

        User user = User.builder()
                .email("example@email.com")
                .password("Password**")
                .nickname("textuser")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        em.persist(user);

        Post post = Post.builder()
                .title("Post")
                .content("Content")
                .user(user)
                .deleted(false)
                .build();

        em.persist(post);

        Comment comment1 = Comment.builder()
                .content("Comment 1")
                .user(user)
                .post(post)
                .deleted(false)
                .build();

        Comment comment2 = Comment.builder()
                .content("Comment 2")
                .user(user)
                .post(post)
                .deleted(true)
                .build();

        em.persist(comment1);
        em.persist(comment2);

        em.flush();
        em.clear();

        int deletedCommentCount = commentRepository.softDeletedCommentsByUserId(user.getId());

        assertThat(deletedCommentCount).isEqualTo(1);
    }

    @Test
    @DisplayName("유저의 삭제되지 않은 모든 댓글 조회")
    void should_ReadAllCommentsOfUser_WhenNotDeleted() {

        User user = User.builder()
                .email("example@email.com")
                .password("Password**")
                .nickname("textuser")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();

        em.persist(user);

        Post post = Post.builder()
                .title("Post")
                .content("Content")
                .user(user)
                .deleted(false)
                .build();

        em.persist(post);

        Comment comment1 = Comment.builder()
                .content("Comment 1")
                .user(user)
                .post(post)
                .deleted(false)
                .build();
        Comment comment2 = Comment.builder()
                .content("Comment 2")
                .user(user)
                .post(post)
                .deleted(true)
                .build();
        Comment comment3 = Comment.builder()
                .content("Comment 3")
                .user(user)
                .post(post)
                .deleted(false)
                .build();

        em.persist(comment1);
        em.persist(comment2);
        em.persist(comment3);

        em.flush();
        em.clear();

        int unDeletedCommentCount = commentRepository.countByUserIdAndDeletedFalse(user.getId());

        assertThat(unDeletedCommentCount).isEqualTo(2);
    }


}
