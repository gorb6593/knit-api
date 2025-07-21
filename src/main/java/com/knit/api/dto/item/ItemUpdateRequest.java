package com.knit.api.dto.item;

import java.math.BigDecimal;
import java.util.List;

public record ItemUpdateRequest(
        String title,
        String content,
        Long price,
        String mode,
        String region,
        BigDecimal latitude,
        BigDecimal longitude,
        List<String> imageUrls,
        int thumbnailIndex
) {}
