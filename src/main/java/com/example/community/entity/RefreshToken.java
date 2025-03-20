package com.example.community.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, columnDefinition = "MEDIUMTEXT")
    private String token;

    @Builder.Default
    @Column(nullable = false)
    private boolean expired = false;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public void updateToken(String token) {
        this.token = token;
    }

    public void expireToken() {
        this.expired = true;
    }
}
