package com.knit.api.dto.item;

public record ItemListResponse(
        Long id,
        String title,
        Long price,
        String region,
        String status,
        String thumbnailUrl,
        int likeCount
) {}