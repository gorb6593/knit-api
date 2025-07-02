package com.knit.api.dto.item;

import java.util.List;

public record ItemUpdateRequest(
        String title,
        String content,
        Long price,
        String region,
        List<String> imageUrls, // 전체 이미지 경로(수정 시 대체)
        int thumbnailIndex
) {}
