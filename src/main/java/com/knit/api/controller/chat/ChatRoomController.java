package com.knit.api.controller.chat;

import com.knit.api.domain.chat.ChatRoom;
import com.knit.api.dto.chat.ChatRoomDto;
import com.knit.api.service.chat.ChatRoomService;
import com.knit.api.service.chat.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatMessageService chatMessageService;

    @GetMapping
    public ResponseEntity<List<ChatRoomDto.Summary>> getChatRooms(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("GET /api/chat/rooms - Getting chat rooms for user: {}", userDetails.getUsername());
        
        Long userId = Long.parseLong(userDetails.getUsername());
        List<ChatRoom> chatRooms = chatRoomService.getUserChatRooms(userId);
        
        List<ChatRoomDto.Summary> summaries = chatRooms.stream()
                .map(room -> {
                    long unreadCount = chatMessageService.getUnreadMessageCount(room.getId(), userId);
                    return ChatRoomDto.Summary.from(room, userId, unreadCount);
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(summaries);
    }

    @PostMapping
    public ResponseEntity<ChatRoomDto> createChatRoom(
            @RequestBody ChatRoomDto.CreateRequest request,
            Authentication authentication) {
        log.info("POST /api/chat/rooms - Creating chat room for item: {}", request.itemId());
        
        Long buyerId = Long.parseLong(authentication.getName());
        ChatRoom chatRoom = chatRoomService.createOrGetChatRoom(request.itemId(), buyerId);
        
        return ResponseEntity.ok(ChatRoomDto.from(chatRoom));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<ChatRoomDto>> getItemChatRooms(@PathVariable Long itemId) {
        log.info("GET /api/chat/rooms/item/{} - Getting chat rooms for item", itemId);
        
        List<ChatRoom> chatRooms = chatRoomService.getItemChatRooms(itemId);
        List<ChatRoomDto> responses = chatRooms.stream()
                .map(ChatRoomDto::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<ChatRoomDto> getChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("GET /api/chat/rooms/{} - Getting chat room details", roomId);
        
        Long userId = Long.parseLong(userDetails.getUsername());
        ChatRoom chatRoom = chatRoomService.getChatRoom(roomId, userId)
                .orElseThrow(() -> new RuntimeException("Chat room not found or access denied"));
        
        return ResponseEntity.ok(ChatRoomDto.from(chatRoom));
    }

    @DeleteMapping("/{roomId}")
    public ResponseEntity<Void> deactivateChatRoom(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("DELETE /api/chat/rooms/{} - Deactivating chat room", roomId);
        
        Long userId = Long.parseLong(userDetails.getUsername());
        chatRoomService.deactivateChatRoom(roomId, userId);
        
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long roomId,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("POST /api/chat/rooms/{}/read - Marking messages as read", roomId);
        
        Long userId = Long.parseLong(userDetails.getUsername());
        chatMessageService.markMessagesAsRead(roomId, userId);
        
        return ResponseEntity.ok().build();
    }
}