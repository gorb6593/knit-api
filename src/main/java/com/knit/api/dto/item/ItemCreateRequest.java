package com.knit.api.dto.item;

import com.knit.api.domain.item.ItemStatus;

import java.math.BigDecimal;
import java.util.List;

public record ItemCreateRequest(
        String title,
        String content,
        Long price,
        ItemStatus mode,
        String region,
        BigDecimal latitude,
        BigDecimal longitude,
        List<String> imageUrls,
        int thumbnailIndex
) {}
