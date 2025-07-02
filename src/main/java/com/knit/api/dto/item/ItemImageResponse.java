package com.knit.api.dto.item;

public record ItemImageResponse(
        Long id,
        String url,
        boolean isThumbnail
) {}
