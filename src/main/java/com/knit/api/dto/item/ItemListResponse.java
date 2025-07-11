package com.knit.api.dto.item;

import java.math.BigDecimal;

public record ItemListResponse(
        Long id,
        String title,
        Long price,
        String region,
        BigDecimal latitude,
        BigDecimal longitude,
        String status,
        String thumbnailUrl,
        Long likeCount
) {}