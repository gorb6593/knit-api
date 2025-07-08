package com.knit.api.service.item;

import com.knit.api.domain.item.*;
import com.knit.api.domain.user.User;
import com.knit.api.dto.item.*;
import com.knit.api.repository.item.*;
import com.knit.api.repository.item.ItemRepositoryCustom;
import com.knit.api.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemLikeRepository itemLikeRepository;
    private final UserRepository userRepository;

    // 아이템 등록
    public Long createItem(Long userId, ItemCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Item item = new Item();
        item.setUser(user);
        item.setTitle(req.title());
        item.setContent(req.content());
        item.setPrice(req.price());
        item.setRegion(req.region());
        item.setStatus(ItemStatus.ON_SALE);

        // 이미지 추가 (추후 multipart로 교체 가능)
        List<ItemImage> images = makeImages(req.imageUrls(), req.thumbnailIndex());
        images.forEach(img -> {
            img.setItem(item);
            item.getImages().add(img);
        });

        itemRepository.save(item);
        return item.getId();
    }

    // 아이템 단건 조회
    @Transactional(readOnly = true)
    public ItemResponse getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        return toItemResponse(item);
    }

    @Transactional(readOnly = true)
    public Page<ItemListResponse> getItemList(Pageable pageable) {
        return itemRepository.findItemsWithThumbnailAndLikeCount(pageable);
    }

    // 아이템 수정 (본인만 가능)
    public void updateItem(Long userId, Long itemId, ItemUpdateRequest req) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getUser().getId().equals(userId))
            throw new IllegalStateException("No permission");

        item.setTitle(req.title());
        item.setContent(req.content());
        item.setPrice(req.price());
        item.setRegion(req.region());

        // 이미지 전체 갱신
        item.getImages().clear();
        List<ItemImage> images = makeImages(req.imageUrls(), req.thumbnailIndex());
        images.forEach(img -> {
            img.setItem(item);
            item.getImages().add(img);
        });
    }

    // 아이템 삭제 (본인만 가능)
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getUser().getId().equals(userId))
            throw new IllegalStateException("No permission");

        itemRepository.delete(item); // cascade로 이미지/좋아요 자동삭제
    }

    // 좋아요 추가/취소
    public void likeItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        Optional<ItemLike> existing = itemLikeRepository.findByUserAndItem(user, item);
        if (existing.isPresent()) {
            itemLikeRepository.delete(existing.get());
        } else {
            ItemLike like = new ItemLike();
            like.setUser(user);
            like.setItem(item);
            itemLikeRepository.save(like);
        }
    }

    // 상태 변경
    public void updateStatus(Long userId, Long itemId, ItemStatus status) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getUser().getId().equals(userId))
            throw new IllegalStateException("No permission");
        item.setStatus(status);
    }

    // ===== 유틸 =====
    private List<ItemImage> makeImages(List<String> urls, int thumbnailIdx) {
        if (urls == null) return List.of();
        return IntStream.range(0, urls.size())
                .mapToObj(i -> {
                    ItemImage img = new ItemImage();
                    img.setUrl(urls.get(i));
                    img.setOriginalFileName(urls.get(i)); // 실제 구현시 변경
                    img.setThumbnail(i == thumbnailIdx);
                    return img;
                })
                .collect(Collectors.toList());
    }

    private ItemResponse toItemResponse(Item item) {
        List<ItemImageResponse> images = item.getImages().stream()
                .map(img -> new ItemImageResponse(img.getId(), img.getUrl(), img.isThumbnail()))
                .collect(Collectors.toList());
        return new ItemResponse(
                item.getId(),
                item.getTitle(),
                item.getContent(),
                item.getPrice(),
                item.getRegion(),
                item.getStatus().name(),
                item.getUser().getId(),
                item.getUser().getNickname(),
                images,
                item.getLikes().size()
        );
    }
}
