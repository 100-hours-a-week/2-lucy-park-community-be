package com.example.community.repository;

import com.example.community.entity.Comment;
import com.example.community.entity.Post;
import com.example.community.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EntityScan(basePackages = "com.example.community.entity")
@ActiveProfiles("test")
public class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    @DisplayName("삭제되지 않은 게시글, 해당 게시글의 댓글까지 조회")
    void should_FindAllPostsWithComments_When_NotDeleted() {

        User user = User.builder()
                .email("example@email.com")
                .password("Password**")
                .nickname("textuser")
                .imageUrl("/uploads/thumbnail_123456789")
                .build();
        em.persist(user);

        Post post1 = Post.builder()
                .title("Post 1")
                .content("Content 1")
                .user(user)
                .deleted(false)
                .build();
        Post post2 = Post.builder()
                .title("Post 2")
                .content("Content 2")
                .user(user)
                .deleted(true)
                .build();

        em.persist(post1);
        em.persist(post2);

        Comment comment1 = Comment.builder()
                .content("Comment 1")
                .user(user)
                .post(post1)
                .build();

        Comment comment2 = Comment.builder()
                .content("Comment 2")
                .user(user)
                .post(post1)
                .build();

        em.persist(comment1);
        em.persist(comment2);

        em.flush();
        em.clear();

        List<Post> result = postRepository.findAllWithComments();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getComments()).hasSize(2);
        assertThat(result.getFirst().getTitle()).isEqualTo("Post 1");
        assertThat(result.getFirst().getComments())
                .extracting(Comment::getContent)
                .containsExactlyInAnyOrder("Comment 1", "Comment 2");
    }
}
