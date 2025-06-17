package com.knit.api.domain.comment;

import com.knit.api.domain.post.Post;
import com.knit.api.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
@Builder
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Post post;

    private LocalDateTime createdAt;

    public static Comment of(String content, User author, Post post) {
        return Comment.builder()
                .content(content)
                .author(author)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
