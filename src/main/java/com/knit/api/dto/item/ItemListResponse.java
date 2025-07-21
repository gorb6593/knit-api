package com.knit.api.dto.item;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemListResponse(
        Long id,
        String title,
        String content,
        Long price,
        String region,
        BigDecimal latitude,
        BigDecimal longitude,
        String status,
        String thumbnailUrl,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,
        Long likeCount
) {}