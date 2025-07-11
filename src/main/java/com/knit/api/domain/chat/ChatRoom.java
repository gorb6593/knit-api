package com.knit.api.domain.chat;

import com.knit.api.domain.common.BaseEntity;
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

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(name = "last_message")
    private String lastMessage;

    @Column(name = "is_active")
    private Boolean isActive;

    public static ChatRoom createOneToOne(User user1, User user2) {
        String roomName = user1.getNickname() + " & " + user2.getNickname();
        return ChatRoom.builder()
                .name(roomName)
                .user1(user1)
                .user2(user2)
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
        return user1.getId().equals(userId) || user2.getId().equals(userId);
    }

    public User getOtherUser(Long userId) {
        if (user1.getId().equals(userId)) {
            return user2;
        } else if (user2.getId().equals(userId)) {
            return user1;
        }
        throw new IllegalArgumentException("User is not a participant of this chat room");
    }
}