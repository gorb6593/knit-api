package com.knit.api.controller.chat;

import com.knit.api.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/chat/debug")
@RequiredArgsConstructor
@Slf4j
public class ChatDebugController {

    private final ChatWebSocketHandler chatWebSocketHandler;

    @GetMapping("/sessions")
    public ResponseEntity<Map<String, Object>> getSessionsDebugInfo() {
        log.info("DEBUG: Getting WebSocket sessions info");
        
        Map<String, Object> debugInfo = new HashMap<>();
        debugInfo.put("roomSessions", chatWebSocketHandler.getRoomSessions());
        debugInfo.put("userSessions", chatWebSocketHandler.getUserSessions());
        debugInfo.put("totalSessions", chatWebSocketHandler.getSessions().size());
        
        // 각 방별 세션 수 계산
        Map<Long, Integer> roomSessionCounts = new HashMap<>();
        chatWebSocketHandler.getRoomSessions().forEach((roomId, sessions) -> {
            roomSessionCounts.put(roomId, sessions.size());
        });
        debugInfo.put("roomSessionCounts", roomSessionCounts);
        
        log.info("DEBUG: Current session state: {}", debugInfo);
        
        return ResponseEntity.ok(debugInfo);
    }
}