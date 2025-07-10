package com.knit.api.dto.item;

public record ItemListResponse(
        Long id,
        String title,
        Long price,
        String region,
        Double latitude,
        Double longitude,
        String status,
        String thumbnailUrl,
        Long likeCount
) {}