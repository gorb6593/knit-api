package com.knit.api.dto.chat;

import com.knit.api.domain.chat.ChatMessage;
import com.knit.api.dto.user.UserDto;

import java.time.LocalDateTime;

public record ChatMessageDto(
        Long id,
        Long chatRoomId,
        UserDto.Summary sender,
        String content,
        String type,
        Boolean isRead,
        LocalDateTime createdAt
) {
    public static ChatMessageDto from(ChatMessage message) {
        return new ChatMessageDto(
                message.getId(),
                message.getChatRoom().getId(),
                message.getSender() != null ? UserDto.Summary.from(message.getSender()) : null,
                message.getContent(),
                message.getType().name(),
                message.getIsRead(),
                message.getCreatedAt()
        );
    }

    public record SendRequest(String content) {}

    public record WebSocketMessage(
            String type,
            Long roomId,
            Long senderId,
            String content,
            String senderNickname,
            LocalDateTime timestamp
    ) {
        public static WebSocketMessage from(ChatMessage message) {
            return new WebSocketMessage(
                    "MESSAGE",
                    message.getChatRoom().getId(),
                    message.getSender() != null ? message.getSender().getId() : null,
                    message.getContent(),
                    message.getSender() != null ? message.getSender().getNickname() : "System",
                    message.getCreatedAt()
            );
        }
    }
}