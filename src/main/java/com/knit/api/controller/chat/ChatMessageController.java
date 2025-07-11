package com.knit.api.controller.chat;

import com.knit.api.domain.chat.ChatMessage;
import com.knit.api.dto.chat.ChatMessageDto;
import com.knit.api.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat/rooms/{roomId}/messages")
@RequiredArgsConstructor
@Slf4j
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    @GetMapping
    public ResponseEntity<Page<ChatMessageDto>> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("GET /api/chat/rooms/{}/messages - Getting messages", roomId);
        
        Long userId = Long.parseLong(userDetails.getUsername());
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<ChatMessage> messages = chatMessageService.getChatMessages(roomId, userId, pageable);
        Page<ChatMessageDto> responseMessages = messages.map(ChatMessageDto::from);
        
        return ResponseEntity.ok(responseMessages);
    }

    @PostMapping
    public ResponseEntity<ChatMessageDto> sendMessage(
            @PathVariable Long roomId,
            @RequestBody ChatMessageDto.SendRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("POST /api/chat/rooms/{}/messages - Sending message", roomId);
        
        Long userId = Long.parseLong(userDetails.getUsername());
        ChatMessage message = chatMessageService.sendMessage(roomId, userId, request.content());
        
        return ResponseEntity.ok(ChatMessageDto.from(message));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadMessageCount(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("GET /api/chat/rooms/{}/messages/unread-count - Getting unread count", roomId);
        
        Long userId = Long.parseLong(userDetails.getUsername());
        long unreadCount = chatMessageService.getUnreadMessageCount(roomId, userId);
        
        return ResponseEntity.ok(unreadCount);
    }
}