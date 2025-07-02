package com.knit.api.repository.item;

import com.knit.api.domain.item.ItemLike;
import com.knit.api.domain.item.Item;
import com.knit.api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemLikeRepository extends JpaRepository<ItemLike, Long> {
    Optional<ItemLike> findByUserAndItem(User user, Item item);
    int countByItem(Item item);
}
