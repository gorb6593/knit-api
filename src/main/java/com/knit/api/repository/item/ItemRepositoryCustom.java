package com.knit.api.repository.item;

import com.knit.api.dto.item.ItemListResponse;
import com.knit.api.dto.item.ItemSearchRequest;
import org.springframework.data.domain.*;

public interface ItemRepositoryCustom {
    Page<ItemListResponse> findItemsWithThumbnailAndLikeCount(ItemSearchRequest searchRequest, Pageable pageable);
}
