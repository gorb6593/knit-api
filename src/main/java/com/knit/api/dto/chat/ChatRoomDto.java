package com.knit.api.dto.chat;

import com.knit.api.domain.chat.ChatRoom;
import com.knit.api.dto.user.UserDto;

import java.time.LocalDateTime;

public record ChatRoomDto(
        Long id,
        String name,
        UserDto.Summary user1,
        UserDto.Summary user2,
        String lastMessage,
        Boolean isActive,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ChatRoomDto from(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.getId(),
                chatRoom.getName(),
                UserDto.Summary.from(chatRoom.getUser1()),
                UserDto.Summary.from(chatRoom.getUser2()),
                chatRoom.getLastMessage(),
                chatRoom.getIsActive(),
                chatRoom.getCreatedAt(),
                chatRoom.getUpdatedAt()
        );
    }

    public record CreateRequest(Long targetUserId) {}

    public record Summary(
            Long id,
            String name,
            UserDto.Summary otherUser,
            String lastMessage,
            long unreadCount,
            LocalDateTime updatedAt
    ) {
        public static Summary from(ChatRoom chatRoom, Long currentUserId, long unreadCount) {
            return new Summary(
                    chatRoom.getId(),
                    chatRoom.getName(),
                    UserDto.Summary.from(chatRoom.getOtherUser(currentUserId)),
                    chatRoom.getLastMessage(),
                    unreadCount,
                    chatRoom.getUpdatedAt()
            );
        }
    }
}