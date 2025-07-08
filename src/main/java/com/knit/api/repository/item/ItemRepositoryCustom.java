package com.knit.api.repository.item;

import com.knit.api.dto.item.ItemListResponse;
import org.springframework.data.domain.*;

public interface ItemRepositoryCustom {
    Page<ItemListResponse> findItemsWithThumbnailAndLikeCount(Pageable pageable);
}
