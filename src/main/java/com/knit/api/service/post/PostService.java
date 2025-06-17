package com.knit.api.service.post;

import com.knit.api.domain.post.Post;
import com.knit.api.domain.user.User;
import com.knit.api.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post create(String title, String content, User author) {
        return postRepository.save(Post.of(title, content, author));
    }
}