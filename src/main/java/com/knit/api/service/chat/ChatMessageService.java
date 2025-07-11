package com.knit.api.service.chat;

import com.knit.api.domain.chat.ChatMessage;
import com.knit.api.domain.chat.ChatRoom;
import com.knit.api.domain.user.User;
import com.knit.api.repository.chat.ChatMessageRepository;
import com.knit.api.repository.chat.ChatRoomRepository;
import com.knit.api.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatRoomService chatRoomService;

    public Page<ChatMessage> getChatMessages(Long roomId, Long userId, Pageable pageable) {
        log.info("Getting chat messages for room: {} by user: {}", roomId, userId);
        
        ChatRoom chatRoom = chatRoomRepository.findActiveRoomByIdAndUserId(roomId, userId)
                .orElseThrow(() -> new RuntimeException("Chat room not found or access denied"));
        
        return chatMessageRepository.findByChatRoomId(roomId, pageable);
    }

    @Transactional
    public ChatMessage sendMessage(Long roomId, Long senderId, String content) {
        log.info("Sending message to room: {} from user: {}", roomId, senderId);
        
        ChatRoom chatRoom = chatRoomRepository.findActiveRoomByIdAndUserId(roomId, senderId)
                .orElseThrow(() -> new RuntimeException("Chat room not found or access denied"));
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found: " + senderId));
        
        ChatMessage message = ChatMessage.createTextMessage(chatRoom, sender, content);
        ChatMessage savedMessage = chatMessageRepository.save(message);
        
        chatRoomService.updateLastMessage(roomId, content);
        
        log.info("Message sent successfully: {}", savedMessage.getId());
        return savedMessage;
    }

    public long getUnreadMessageCount(Long roomId, Long userId) {
        log.info("Getting unread message count for room: {} by user: {}", roomId, userId);
        return chatMessageRepository.countUnreadMessages(roomId, userId);
    }

    @Transactional
    public void markMessagesAsRead(Long roomId, Long userId) {
        log.info("Marking messages as read for room: {} by user: {}", roomId, userId);
        
        List<ChatMessage> unreadMessages = chatMessageRepository.findUnreadMessages(roomId, userId);
        unreadMessages.forEach(ChatMessage::markAsRead);
        
        if (!unreadMessages.isEmpty()) {
            chatMessageRepository.saveAll(unreadMessages);
            log.info("Marked {} messages as read", unreadMessages.size());
        }
    }
}