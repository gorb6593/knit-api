package com.knit.api.service.chat;

import com.knit.api.domain.chat.ChatRoom;
import com.knit.api.domain.item.Item;
import com.knit.api.domain.user.User;
import com.knit.api.repository.chat.ChatRoomRepository;
import com.knit.api.repository.item.ItemRepository;
import com.knit.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public List<ChatRoom> getUserChatRooms(Long userId) {
        log.info("Getting chat rooms for user: {}", userId);
        return chatRoomRepository.findActiveRoomsWithLastMessage(userId);
    }

    @Transactional
    public ChatRoom createOrGetChatRoom(Long itemId, Long buyerId) {
        log.info("Creating or getting chat room for item: {} by buyer: {}", itemId, buyerId);

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found: " + itemId));

        User seller = item.getUser();
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("User not found: " + buyerId));

        if (seller.getId().equals(buyerId)) {
            throw new IllegalArgumentException("Cannot create chat room with yourself");
        }

        Optional<ChatRoom> existingRoom = chatRoomRepository.findActiveRoomByItemAndBuyer(itemId, buyerId);
        if (existingRoom.isPresent()) {
            log.info("Found existing chat room: {}", existingRoom.get().getId());
            return existingRoom.get();
        }

        ChatRoom chatRoom = ChatRoom.createItemChat(item, seller, buyer);
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        
        log.info("Created new chat room: {} for item: {}", savedRoom.getId(), itemId);
        return savedRoom;
    }

    public Optional<ChatRoom> getChatRoom(Long roomId, Long userId) {
        log.info("Getting chat room: {} for user: {}", roomId, userId);
        return chatRoomRepository.findActiveRoomByIdAndUserId(roomId, userId);
    }

    public List<ChatRoom> getItemChatRooms(Long itemId) {
        log.info("Getting chat rooms for item: {}", itemId);
        return chatRoomRepository.findActiveRoomsByItemId(itemId);
    }

    @Transactional
    public void updateLastMessage(Long roomId, String message) {
        log.info("Updating last message for room: {}", roomId);
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Chat room not found: " + roomId));
        
        chatRoom.updateLastMessage(message);
        chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public void deactivateChatRoom(Long roomId, Long userId) {
        log.info("Deactivating chat room: {} by user: {}", roomId, userId);
        ChatRoom chatRoom = chatRoomRepository.findActiveRoomByIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("Chat room not found or access denied"));
        
        chatRoom.deactivate();
        chatRoomRepository.save(chatRoom);
    }
}