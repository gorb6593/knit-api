package com.knit.api.repository.chat;

import com.knit.api.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long>, ChatRoomRepositoryCustom {

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.user1.id = :userId OR cr.user2.id = :userId) AND cr.isActive = true ORDER BY cr.updatedAt DESC")
    List<ChatRoom> findActiveRoomsByUserId(@Param("userId") Long userId);

    @Query("SELECT cr FROM ChatRoom cr WHERE ((cr.user1.id = :user1Id AND cr.user2.id = :user2Id) OR (cr.user1.id = :user2Id AND cr.user2.id = :user1Id)) AND cr.isActive = true")
    Optional<ChatRoom> findActiveRoomByUsers(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id = :roomId AND (cr.user1.id = :userId OR cr.user2.id = :userId) AND cr.isActive = true")
    Optional<ChatRoom> findActiveRoomByIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);
}