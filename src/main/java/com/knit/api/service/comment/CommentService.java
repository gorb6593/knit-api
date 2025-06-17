package com.knit.api.service.comment;

import com.knit.api.domain.comment.Comment;
import com.knit.api.domain.post.Post;
import com.knit.api.domain.user.User;
import com.knit.api.repository.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Optional<Comment> findById(Long id) {
        return commentRepository.findById(id);
    }

    @Transactional
    public Comment create(String content, User author, Post post) {
        return commentRepository.save(Comment.of(content, author, post));
    }
}