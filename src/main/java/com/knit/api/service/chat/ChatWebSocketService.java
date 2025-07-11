package com.knit.api.service.chat;

import com.knit.api.domain.chat.ChatMessage;
import com.knit.api.dto.chat.ChatMessageDto;
import com.knit.api.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketService {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ChatMessageService chatMessageService;

    public void sendMessage(Long roomId, Long senderId, String content) {
        try {
            ChatMessage message = chatMessageService.sendMessage(roomId, senderId, content);
            
            ChatMessageDto.WebSocketMessage wsMessage = ChatMessageDto.WebSocketMessage.from(message);
            
            chatWebSocketHandler.sendMessageToUser(senderId, wsMessage);
            
            log.info("Message sent via WebSocket to room: {}", roomId);
        } catch (Exception e) {
            log.error("Error sending message via WebSocket: {}", e.getMessage());
        }
    }
}