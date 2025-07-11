package com.knit.api.domain.chat;

import com.knit.api.domain.common.BaseEntity;
import com.knit.api.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chat_messages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(nullable = false, length = 1000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(name = "is_read")
    private Boolean isRead;

    public enum MessageType {
        TEXT, IMAGE, FILE, SYSTEM
    }

    public static ChatMessage createTextMessage(ChatRoom chatRoom, User sender, String content) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .type(MessageType.TEXT)
                .isRead(false)
                .build();
    }

    public static ChatMessage createSystemMessage(ChatRoom chatRoom, String content) {
        return ChatMessage.builder()
                .chatRoom(chatRoom)
                .content(content)
                .type(MessageType.SYSTEM)
                .isRead(false)
                .build();
    }

    public void markAsRead() {
        this.isRead = true;
    }
}