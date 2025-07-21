package com.knit.api.service.item;

import com.knit.api.domain.item.*;
import com.knit.api.domain.user.User;
import com.knit.api.dto.item.*;
import com.knit.api.repository.item.*;
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

import static java.time.LocalDateTime.now;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemLikeRepository itemLikeRepository;
    private final UserRepository userRepository;

    // 아이템 등록
    public void createItem(Long userId, ItemCreateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Item item = new Item();
        item.setUser(user);
        item.setTitle(req.title());
        item.setContent(req.content());
        item.setPrice(req.price());
        item.setRegion(req.region());
        item.setLatitude(req.latitude());
        item.setLongitude(req.longitude());
        item.setStatus(req.mode());
        List<ItemImage> images = makeImages(req.imageUrls(), req.thumbnailIndex());
        images.forEach(img -> {
            img.setItem(item);
            item.getImages().add(img);
        });

        itemRepository.save(item);
    }

    // 아이템 단건 조회
    @Transactional(readOnly = true)
    public ItemResponse getItem(Long itemId) {
        return toItemResponse(itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Item not found")));
    }

    @Transactional(readOnly = true)
    public Page<ItemListResponse> getItemList(Pageable pageable) {
        return itemRepository.findItemsWithThumbnailAndLikeCount(pageable);
    }

    // 아이템 수정
    public void updateItem(Long userId, Long itemId, ItemUpdateRequest req) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new EntityNotFoundException("Item not found"));
        if (!item.getUser().getId().equals(userId))
            throw new IllegalStateException("No permission");

        item.setTitle(req.title());
        item.setContent(req.content());
        item.setPrice(req.price());
        item.setRegion(req.region());
        item.setLatitude(req.latitude());
        item.setLongitude(req.longitude());
        item.setStatus(ItemStatus.valueOf(req.mode()));

        // 이미지 전체 갱신
        item.getImages().clear();
        List<ItemImage> images = makeImages(req.imageUrls(), req.thumbnailIndex());
        images.forEach(img -> {
            img.setItem(item);
            item.getImages().add(img);
        });
    }

    // 아이템 삭제
    public void deleteItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("Item not found"));

        // 이미 삭제된 아이템인지 확인
        if (item.getDeletedAt() != null) {
            throw new IllegalStateException("Item already deleted");
        }

        if (!item.getUser().getId().equals(userId)) {
            throw new IllegalStateException("No permission");
        }

        // 소프트 삭제 - deletedAt 설정
        item.setDeletedAt(now());
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

    private List<ItemImage> makeImages(List<String> urls, int thumbnailIdx) {
        if (urls == null) return List.of();
        return IntStream.range(0, urls.size())
                .mapToObj(i -> {
                    ItemImage img = new ItemImage();
                    img.setUrl(urls.get(i));
                    img.setOriginalFileName(urls.get(i));
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
                item.getLatitude(),
                item.getLongitude(),
                item.getStatus().name(),
                item.getUser().getId(),
                item.getUser().getNickname(),
                images,
                item.getLikes().size()
        );
    }
}
