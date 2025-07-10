package com.knit.api.dto.item;

import java.util.List;

public record ItemResponse(
        Long id,
        String title,
        String content,
        Long price,
        String region,
        Double latitude,
        Double longitude,
        String status,
        Long userId,
        String userNickname,
        List<ItemImageResponse> images,
        int likeCount
) {}
