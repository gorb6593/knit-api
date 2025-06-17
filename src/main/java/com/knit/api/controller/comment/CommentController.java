package com.knit.api.controller.comment;

import com.knit.api.domain.comment.Comment;
import com.knit.api.domain.post.Post;
import com.knit.api.domain.user.User;
import com.knit.api.dto.comment.CommentDto;
import com.knit.api.service.comment.CommentService;
import com.knit.api.service.post.PostService;
import com.knit.api.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final PostService postService;
    private final UserService userService;

    // 댓글 등록(임시: userId, postId로 작성자/글 지정)
    @PostMapping
    public CommentDto create(@RequestBody CommentDto dto, @RequestParam Long userId, @RequestParam Long postId) {
        User author = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("작성자를 찾을 수 없습니다."));
        Post post = postService.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Comment comment = commentService.create(dto.content(), author, post);
        return CommentDto.from(comment);
    }

    // 댓글 조회
    @GetMapping("/{id}")
    public CommentDto findById(@PathVariable Long id) {
        return commentService.findById(id)
                .map(CommentDto::from)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));
    }
}