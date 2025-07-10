package com.knit.api.dto.item;

import java.util.List;

public record ItemUpdateRequest(
        String title,
        String content,
        Long price,
        String region,
        Double latitude,
        Double longitude,
        List<String> imageUrls,
        int thumbnailIndex
) {}
