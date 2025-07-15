package com.knit.api.controller.item;

import com.knit.api.dto.item.*;
import com.knit.api.domain.item.ItemStatus;
import com.knit.api.service.item.ItemService;
import com.knit.api.config.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/items")
@RestController
public class ItemController {

    private final ItemService itemService;
    private final S3Service s3Service;

    // 파일 업로드 (단일)
    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileUrl = s3Service.uploadFile(file);
            return ResponseEntity.ok(Map.of("url", fileUrl));
        } catch (IOException e) {
            log.error("File upload failed", e);
            return ResponseEntity.badRequest().body(Map.of("error", "File upload failed: " + e.getMessage()));
        }
    }

    // 파일 업로드 (다중)
    @PostMapping("/upload/multiple")
    public ResponseEntity<Map<String, List<String>>> uploadFiles(@RequestParam("files") List<MultipartFile> files) {
        log.info("Uploading files");
        try {
            List<String> fileUrls = s3Service.uploadFiles(files);
            return ResponseEntity.ok(Map.of("urls", fileUrls));
        } catch (Exception e) {
            log.error("Files upload failed", e);
            return ResponseEntity.badRequest().body(Map.of("error", List.of("Files upload failed: " + e.getMessage())));
        }
    }

    // 등록 (JSON)
    @PostMapping
    public ResponseEntity<Void> createItem(Authentication authentication, @RequestBody ItemCreateRequest request) {
        itemService.createItem(Long.valueOf(authentication.getName()), request);
        return ResponseEntity.ok().build();
    }

    // 등록 (Multipart Form)
    @PostMapping("/form")
    public ResponseEntity<Void> createItemWithFiles(
            Authentication authentication,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("price") Long price,
            @RequestParam("region") String region,
            @RequestParam(value = "latitude", required = false) BigDecimal latitude,
            @RequestParam(value = "longitude", required = false) BigDecimal longitude,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            @RequestParam(value = "thumbnailIndex", defaultValue = "0") int thumbnailIndex) {
        
        try {
            List<String> imageUrls = files != null && !files.isEmpty() ? s3Service.uploadFiles(files) : List.of();
            
            ItemCreateRequest request = new ItemCreateRequest(
                    title, content, price, region, latitude, longitude, imageUrls, thumbnailIndex
            );
            
            itemService.createItem(Long.valueOf(authentication.getName()), request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Item creation failed", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // 목록(페이징)
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
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody ItemUpdateRequest request) {
        itemService.updateItem(Long.valueOf(authentication.getName()), id, request);
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
