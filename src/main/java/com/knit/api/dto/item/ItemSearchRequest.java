package com.knit.api.dto.item;

import com.knit.api.domain.item.ItemStatus;
import java.math.BigDecimal;

public record ItemSearchRequest(
        String keyword,           // 제목, 내용 검색
        ItemStatus status,        // 상태 필터 (LOST, FOUND, COMPLETED)
        BigDecimal latitude,      // 내 현재 위치 위도
        BigDecimal longitude,     // 내 현재 위치 경도
        Double radiusKm          // 반경 (km) - 기본값 10km
) {
    public ItemSearchRequest {
        // 기본값 설정
        if (radiusKm == null) {
            radiusKm = 10.0;
        }
    }
}
