package com.knit.api.domain.item;

import com.knit.api.domain.user.User;
import com.knit.api.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Item extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user; // 작성자

    private String title;
    private String content;
    private Long price;
    private String region;

    @Enumerated(EnumType.STRING)
    private ItemStatus status; // 판매중, 예약중, 거래완료 등

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLike> likes = new ArrayList<>();
}
