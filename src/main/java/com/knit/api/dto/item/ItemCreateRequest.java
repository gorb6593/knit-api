package com.knit.api.dto.item;

import java.util.List;

public record ItemCreateRequest(
        String title,
        String content,
        Long price,
        String region,
        List<String> imageUrls, // 실제로는 MultipartFile이지만 예시로 경로 리스트만
        int thumbnailIndex      // 대표 이미지 지정(인덱스 기반)
) {}
