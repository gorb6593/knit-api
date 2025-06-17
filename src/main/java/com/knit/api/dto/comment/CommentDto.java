package com.knit.api.dto.comment;

import java.time.LocalDateTime;
import com.knit.api.domain.comment.Comment;

public record CommentDto(
        Long id,
        String content,
        Long authorId,
        String authorNickname,
        Long postId,
        LocalDateTime createdAt
) {
    public static CommentDto from(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getNickname(),
                comment.getPost().getId(),
                comment.getCreatedAt()
        );
    }
}
