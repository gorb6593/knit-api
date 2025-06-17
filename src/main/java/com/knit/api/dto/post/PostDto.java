package com.knit.api.dto.post;

import java.time.LocalDateTime;
import com.knit.api.domain.post.Post;

public record PostDto(
        Long id,
        String title,
        String content,
        Long authorId,
        String authorNickname,
        LocalDateTime createdAt
) {
    public static PostDto from(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getAuthor().getId(),
                post.getAuthor().getNickname(),
                post.getCreatedAt()
        );
    }
}
