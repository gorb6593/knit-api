package com.knit.api.controller.item;

import com.knit.api.dto.item.*;
import com.knit.api.domain.item.ItemStatus;
import com.knit.api.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/items")
@RestController
public class ItemController {

    private final ItemService itemService;

    // 등록
    @PostMapping
    public void createItem(Authentication authentication, @RequestBody ItemCreateRequest request) {
        itemService.createItem(Long.valueOf(authentication.getName()), request);
    }

    // 목록
    @GetMapping
    public ResponseEntity<Page<ItemListResponse>> getItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(itemService.getItemList(pageable));
    }

    // 상세
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponse> getItem(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItem(id));
    }

    // 수정
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateItem(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request) {
        itemService.updateItem(userId, id, request);
        return ResponseEntity.ok().build();
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long id) {
        itemService.deleteItem(userId, id);
        return ResponseEntity.ok().build();
    }

    // 좋아요(토글)
    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeItem(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long id) {
        itemService.likeItem(userId, id);
        return ResponseEntity.ok().build();
    }

    // 상태변경
    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @RequestHeader("X-USER-ID") Long userId,
            @PathVariable Long id,
            @RequestParam ItemStatus status) {
        itemService.updateStatus(userId, id, status);
        return ResponseEntity.ok().build();
    }
}
