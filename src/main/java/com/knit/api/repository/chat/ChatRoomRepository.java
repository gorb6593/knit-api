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

    @Query("SELECT cr FROM ChatRoom cr WHERE (cr.seller.id = :userId OR cr.buyer.id = :userId) AND cr.isActive = true ORDER BY cr.updatedAt DESC")
    List<ChatRoom> findActiveRoomsByUserId(@Param("userId") Long userId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.item.id = :itemId AND cr.buyer.id = :buyerId AND cr.isActive = true")
    Optional<ChatRoom> findActiveRoomByItemAndBuyer(@Param("itemId") Long itemId, @Param("buyerId") Long buyerId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.id = :roomId AND (cr.seller.id = :userId OR cr.buyer.id = :userId) AND cr.isActive = true")
    Optional<ChatRoom> findActiveRoomByIdAndUserId(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.item.id = :itemId AND cr.isActive = true")
    List<ChatRoom> findActiveRoomsByItemId(@Param("itemId") Long itemId);
}