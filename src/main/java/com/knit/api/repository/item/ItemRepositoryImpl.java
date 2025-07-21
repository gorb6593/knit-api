package com.knit.api.repository.item;

import com.knit.api.domain.item.QItem;
import com.knit.api.domain.item.QItemImage;
import com.knit.api.domain.item.QItemLike;
import com.knit.api.dto.item.ItemListResponse;
import com.knit.api.dto.item.ItemSearchRequest;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ItemListResponse> findItemsWithThumbnailAndLikeCount(ItemSearchRequest searchRequest, Pageable pageable) {
        QItem item = QItem.item;
        QItemImage image = QItemImage.itemImage;
        QItemLike like = QItemLike.itemLike;

        // 검색 조건 빌더
        BooleanBuilder whereClause = buildSearchConditions(item, searchRequest);

        List<ItemListResponse> content = queryFactory
                .select(item.id,
                        item.title,
                        item.content,
                        item.price,
                        item.region,
                        item.latitude,
                        item.longitude,
                        item.status.stringValue(),
                        item.createdAt,
                        image.url,
                        like.countDistinct())
                .from(item)
                .leftJoin(image).on(image.item.eq(item),
                        image.isThumbnail.isTrue(),
                        image.deletedAt.isNull())
                .leftJoin(like).on(like.item.eq(item),
                        like.deletedAt.isNull())
                .where(whereClause)
                .orderBy(item.id.desc())
                .groupBy(item.id, image.url, item.title, item.price, item.region, item.status)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new ItemListResponse(
                        tuple.get(item.id),
                        tuple.get(item.title),
                        tuple.get(item.content),
                        tuple.get(item.price),
                        tuple.get(item.region),
                        tuple.get(item.latitude),
                        tuple.get(item.longitude),
                        tuple.get(item.status.stringValue()),
                        tuple.get(image.url),
                        tuple.get(item.createdAt),
                        tuple.get(like.countDistinct())
                ))
                .toList();

        // total count
        Long total = queryFactory
                .select(item.id.countDistinct())
                .from(item)
                .where(whereClause)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    private BooleanBuilder buildSearchConditions(QItem item, ItemSearchRequest searchRequest) {
        BooleanBuilder whereClause = new BooleanBuilder();

        // 기본 조건: 삭제되지 않은 아이템만
        whereClause.and(item.deletedAt.isNull());

        // 검색 요청이 null인 경우 기본 조건만 반환
        if (searchRequest == null) {
            return whereClause;
        }

        // 키워드 검색 (제목, 내용)
        if (StringUtils.hasText(searchRequest.keyword())) {
            whereClause.and(
                    item.title.containsIgnoreCase(searchRequest.keyword())
                            .or(item.content.containsIgnoreCase(searchRequest.keyword()))
            );
        }

        // 상태 필터
        if (searchRequest.status() != null) {
            whereClause.and(item.status.eq(searchRequest.status()));
        }

        // 위치 기반 검색 (직선거리 기준 박스 필터링)
        if (searchRequest.latitude() != null && searchRequest.longitude() != null) {
            addLocationFilter(whereClause, item, searchRequest);
        }

        return whereClause;
    }

    private void addLocationFilter(BooleanBuilder whereClause, QItem item, ItemSearchRequest searchRequest) {
        BigDecimal userLat = searchRequest.latitude();
        BigDecimal userLon = searchRequest.longitude();
        double radiusKm = searchRequest.radiusKm() != null ? searchRequest.radiusKm() : 10.0;

        // 위도/경도를 직선거리로 근사 계산
        // 1도 ≈ 111km, 경도는 위도에 따라 조정
        double latDiff = radiusKm / 111.0;
        double lonDiff = radiusKm / (111.0 * Math.cos(Math.toRadians(userLat.doubleValue())));

        // 박스 형태로 필터링 (정확하지는 않지만 성능이 좋음)
        BigDecimal minLat = userLat.subtract(BigDecimal.valueOf(latDiff));
        BigDecimal maxLat = userLat.add(BigDecimal.valueOf(latDiff));
        BigDecimal minLon = userLon.subtract(BigDecimal.valueOf(lonDiff));
        BigDecimal maxLon = userLon.add(BigDecimal.valueOf(lonDiff));

        whereClause.and(item.latitude.between(minLat, maxLat));
        whereClause.and(item.longitude.between(minLon, maxLon));
    }
}