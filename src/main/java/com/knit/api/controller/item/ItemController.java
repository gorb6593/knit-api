package com.knit.api.controller.item;

import com.knit.api.dto.item.*;
import com.knit.api.domain.item.ItemStatus;
import com.knit.api.service.item.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Long> createItem(
            @RequestHeader("X-USER-ID") Long userId, // 인증 처리 예시
            @RequestBody ItemCreateRequest request) {
        Long itemId = itemService.createItem(userId, request);
        return ResponseEntity.ok(itemId);
    }

    // 목록
    @GetMapping
    public ResponseEntity<List<ItemListResponse>> getItems() {
        return ResponseEntity.ok(itemService.getItemList());
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
