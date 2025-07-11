package com.knit.api.service.chat;

import com.knit.api.domain.chat.ChatRoom;
import com.knit.api.domain.user.User;
import com.knit.api.repository.chat.ChatRoomRepository;
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

    public List<ChatRoom> getUserChatRooms(Long userId) {
        log.info("Getting chat rooms for user: {}", userId);
        return chatRoomRepository.findActiveRoomsWithLastMessage(userId);
    }

    @Transactional
    public ChatRoom createOrGetChatRoom(Long user1Id, Long user2Id) {
        log.info("Creating or getting chat room between users: {} and {}", user1Id, user2Id);

        Optional<ChatRoom> existingRoom = chatRoomRepository.findActiveRoomByUsers(user1Id, user2Id);
        if (existingRoom.isPresent()) {
            log.info("Found existing chat room: {}", existingRoom.get().getId());
            return existingRoom.get();
        }

        User user1 = userRepository.findById(user1Id)
                .orElseThrow(() -> new RuntimeException("User not found: " + user1Id));
        User user2 = userRepository.findById(user2Id)
                .orElseThrow(() -> new RuntimeException("User not found: " + user2Id));

        ChatRoom chatRoom = ChatRoom.createOneToOne(user1, user2);
        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);
        
        log.info("Created new chat room: {}", savedRoom.getId());
        return savedRoom;
    }

    public Optional<ChatRoom> getChatRoom(Long roomId, Long userId) {
        log.info("Getting chat room: {} for user: {}", roomId, userId);
        return chatRoomRepository.findActiveRoomByIdAndUserId(roomId, userId);
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