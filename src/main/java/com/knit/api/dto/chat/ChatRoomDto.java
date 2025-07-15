package com.knit.api.dto.chat;

import com.knit.api.domain.chat.ChatRoom;
import com.knit.api.dto.user.UserDto;

import java.time.LocalDateTime;

public record ChatRoomDto(
        Long id,
        Long itemId,
        String itemTitle,
        UserDto.Summary seller,
        UserDto.Summary buyer,
        String lastMessage,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ChatRoomDto from(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getItem().getId(),
                chatRoom.getItem().getTitle(),
                UserDto.Summary.from(chatRoom.getSeller()),
                UserDto.Summary.from(chatRoom.getBuyer()),
                chatRoom.getLastMessage(),
                chatRoom.getIsActive(),
                chatRoom.getCreatedAt(),
                chatRoom.getUpdatedAt()
        );
    }

    public record CreateRequest(Long itemId) {}

    public record Summary(
            Long id,
            Long itemId,
            String itemTitle,
            UserDto.Summary otherUser,
            String lastMessage,
            long unreadCount,
            LocalDateTime updatedAt
    ) {
        public static Summary from(ChatRoom chatRoom, Long currentUserId, long unreadCount) {
            return new Summary(
                    chatRoom.getId(),
                    chatRoom.getItem().getId(),
                    chatRoom.getItem().getTitle(),
                    UserDto.Summary.from(chatRoom.getOtherUser(currentUserId)),
                    chatRoom.getLastMessage(),
                    unreadCount,
                    chatRoom.getUpdatedAt()
            );
        }
    }
}