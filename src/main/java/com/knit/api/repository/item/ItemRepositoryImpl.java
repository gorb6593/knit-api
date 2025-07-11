package com.knit.api.repository.item;

import com.knit.api.domain.item.QItem;
import com.knit.api.domain.item.QItemImage;
import com.knit.api.domain.item.QItemLike;
import com.knit.api.dto.item.ItemListResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ItemRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    @Override
    public Page<ItemListResponse> findItemsWithThumbnailAndLikeCount(Pageable pageable) {
        QItem item = QItem.item;
        QItemImage image = QItemImage.itemImage;
        QItemLike like = QItemLike.itemLike;

        // 1. 쿼리 정의
        List<ItemListResponse> content = queryFactory
                .select(item.id,
                        item.title,
                        item.price,
                        item.region,
                        item.latitude,
                        item.longitude,
                        item.status.stringValue(),
                        image.url,
                        like.countDistinct())
                .from(item)
                .leftJoin(image).on(image.item.eq(item), image.isThumbnail.isTrue())
                .leftJoin(like).on(like.item.eq(item))
                .orderBy(item.id.desc())
                .groupBy(item.id, image.url, item.title, item.price, item.region, item.status)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(tuple -> new ItemListResponse(
                        tuple.get(item.id),
                        tuple.get(item.title),
                        tuple.get(item.price),
                        tuple.get(item.region),
                        tuple.get(item.latitude),
                        tuple.get(item.longitude),
                        tuple.get(item.status.stringValue()),
                        tuple.get(image.url),
                        tuple.get(like.countDistinct())
                ))
                .toList();

        // 2. total count (group by면 countDistinct 필요)
        Long total = queryFactory
                .select(item.id.countDistinct())
                .from(item)
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }
}
