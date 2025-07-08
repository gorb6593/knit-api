package com.knit.api.dto.item;

import java.util.List;

public record ItemCreateRequest(
        String title,
        String content,
        Long price,
        String region,
        List<String> imageUrls,
        int thumbnailIndex
) {}
