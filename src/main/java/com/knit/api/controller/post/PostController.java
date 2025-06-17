package com.knit.api.controller.post;

import com.knit.api.domain.post.Post;
import com.knit.api.domain.user.User;
import com.knit.api.dto.post.PostDto;
import com.knit.api.service.post.PostService;
import com.knit.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final UserService userService;

    // 게시글 등록(임시: userId로 author 지정)
    @PostMapping
    public PostDto create(@RequestBody PostDto dto, @RequestParam Long userId) {
        User author = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("작성자를 찾을 수 없습니다."));
        Post post = postService.create(dto.title(), dto.content(), author);
        return PostDto.from(post);
    }

    // 게시글 조회
    @GetMapping("/{id}")
    public PostDto findById(@PathVariable Long id) {
        return postService.findById(id)
                .map(PostDto::from)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }
}