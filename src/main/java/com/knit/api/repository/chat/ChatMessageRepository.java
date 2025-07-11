package com.knit.api.repository.chat;

import com.knit.api.domain.chat.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId ORDER BY cm.createdAt DESC")
    Page<ChatMessage> findByChatRoomId(@Param("roomId") Long roomId, Pageable pageable);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId ORDER BY cm.createdAt DESC")
    List<ChatMessage> findByChatRoomIdOrderByCreatedAtDesc(@Param("roomId") Long roomId);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.sender.id != :userId AND cm.isRead = false")
    long countUnreadMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("SELECT cm FROM ChatMessage cm WHERE cm.chatRoom.id = :roomId AND cm.sender.id != :userId AND cm.isRead = false")
    List<ChatMessage> findUnreadMessages(@Param("roomId") Long roomId, @Param("userId") Long userId);
}