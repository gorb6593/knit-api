package com.knit.api.repository.chat;

import com.knit.api.domain.chat.ChatRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatRoomRepositoryCustom {
    List<ChatRoom> findActiveRoomsWithLastMessage(Long userId);
    Page<ChatRoom> findActiveRoomsWithPaging(Long userId, Pageable pageable);
    List<ChatRoom> findRoomsWithUnreadCount(Long userId);
}