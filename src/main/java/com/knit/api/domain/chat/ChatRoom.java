package com.knit.api.domain.chat;

import com.knit.api.domain.common.BaseEntity;
import com.knit.api.domain.item.Item;
import com.knit.api.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "is_active")
    private Boolean isActive;

    public static ChatRoom createItemChat(Item item, User seller, User buyer) {
        return ChatRoom.builder()
                .item(item)
                .seller(seller)
                .buyer(buyer)
                .isActive(true)
                .build();
    }

    public void updateLastMessage(String message) {
        this.lastMessage = message;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public boolean isParticipant(Long userId) {
        return seller.getId().equals(userId) || buyer.getId().equals(userId);
    }

    public User getOtherUser(Long userId) {
        if (seller.getId().equals(userId)) {
            return buyer;
        } else if (buyer.getId().equals(userId)) {
            return seller;
        }
        throw new IllegalArgumentException("User is not a participant of this chat room");
    }

    public String getRoomName() {
        return item.getTitle() + " - " + seller.getNickname() + " & " + buyer.getNickname();
    }
}