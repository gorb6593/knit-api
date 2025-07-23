package com.knit.api.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.knit.api.dto.chat.ChatMessageDto;
import com.knit.api.util.JwtProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    
    public ChatWebSocketHandler(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        this.objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();
    private final Map<Long, Set<String>> roomSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket connection established: {} from {}", session.getId(), session.getRemoteAddress());
        log.info("WebSocket handshake headers: {}", session.getHandshakeHeaders());
        log.info("WebSocket URI: {}", session.getUri());
        
        try {
            String token = extractTokenFromSession(session);
            log.info("Extracted token: {}", token != null ? "Found" : "Not found");
            
            if (token != null && jwtProvider.validateToken(token)) {
                String userIdStr = jwtProvider.getUserId(token);
                Long userId = Long.parseLong(userIdStr);
                
                sessions.put(session.getId(), session);
                userSessions.put(userIdStr, session.getId());
                session.getAttributes().put("userId", userId);
                
                log.info("User {} connected via WebSocket successfully", userId);
            } else {
                log.warn("Invalid or missing token for WebSocket connection: {}", session.getId());
                log.warn("Token validation result: {}", token != null ? jwtProvider.validateToken(token) : "Token is null");
                session.close(CloseStatus.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            log.error("Error during WebSocket connection establishment: {}", e.getMessage(), e);
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("Received message from session {}: {}", session.getId(), message.getPayload());
        
        try {
            Long userId = (Long) session.getAttributes().get("userId");
            if (userId == null) {
                log.warn("No user ID found in session attributes");
                return;
            }

            ChatMessageDto.WebSocketMessage wsMessage = objectMapper.readValue(
                message.getPayload(), 
                ChatMessageDto.WebSocketMessage.class
            );

            if ("JOIN_ROOM".equals(wsMessage.type())) {
                joinRoom(session, wsMessage.roomId());
                
                // 방 참가 성공 응답 전송
                ChatMessageDto.WebSocketMessage joinResponse = new ChatMessageDto.WebSocketMessage(
                    "JOIN_ROOM_SUCCESS",
                    wsMessage.roomId(),
                    userId,
                    "방에 입장했습니다.",
                    null,
                    java.time.LocalDateTime.now()
                );
                
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(joinResponse)));
                    log.info("Sent JOIN_ROOM_SUCCESS to user: {} for room: {}", userId, wsMessage.roomId());
                } catch (IOException e) {
                    log.error("Error sending JOIN_ROOM_SUCCESS: {}", e.getMessage());
                }
                
            } else if ("LEAVE_ROOM".equals(wsMessage.type())) {
                leaveRoom(session, wsMessage.roomId());
            } else if ("MESSAGE".equals(wsMessage.type())) {
                // 웹소켓을 통한 직접 메시지 전송은 REST API를 통해 처리하는 것을 권장
                log.info("Received direct message via WebSocket from user: {} to room: {}", userId, wsMessage.roomId());
            }
            
        } catch (Exception e) {
            log.error("Error handling WebSocket message: {}", e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket connection closed: {} with status: {}", session.getId(), status);
        
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            String userIdStr = userId.toString();
            sessions.remove(session.getId());
            userSessions.remove(userIdStr);
            
            roomSessions.values().forEach(roomSessionSet -> roomSessionSet.remove(session.getId()));
            
            log.info("User {} disconnected from WebSocket", userId);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket transport error for session {}: {}", session.getId(), exception.getMessage());
        session.close(CloseStatus.SERVER_ERROR);
    }

    private String extractTokenFromSession(WebSocketSession session) {
        // 1. 쿼리 파라미터에서 토큰 추출
        URI uri = session.getUri();
        if (uri != null) {
            String query = uri.getQuery();
            if (query != null && query.contains("token=")) {
                String[] params = query.split("&");
                for (String param : params) {
                    if (param.startsWith("token=")) {
                        return param.substring(6);
                    }
                }
            }
        }
        
        // 2. 헤더에서 토큰 추출 (Authorization: Bearer TOKEN)
        if (session.getHandshakeHeaders().containsKey("Authorization")) {
            String authHeader = session.getHandshakeHeaders().getFirst("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                return authHeader.substring(7);
            }
        }
        
        // 3. 커스텀 헤더에서 토큰 추출 (X-Auth-Token)
        if (session.getHandshakeHeaders().containsKey("X-Auth-Token")) {
            return session.getHandshakeHeaders().getFirst("X-Auth-Token");
        }
        
        return null;
    }

    private void joinRoom(WebSocketSession session, Long roomId) {
        roomSessions.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session.getId());
        session.getAttributes().put("currentRoomId", roomId);
        log.info("Session {} joined room {}", session.getId(), roomId);
    }

    private void leaveRoom(WebSocketSession session, Long roomId) {
        Set<String> roomSessionSet = roomSessions.get(roomId);
        if (roomSessionSet != null) {
            roomSessionSet.remove(session.getId());
            if (roomSessionSet.isEmpty()) {
                roomSessions.remove(roomId);
            }
        }
        session.getAttributes().remove("currentRoomId");
        log.info("Session {} left room {}", session.getId(), roomId);
    }

    public void sendMessageToUser(Long userId, ChatMessageDto.WebSocketMessage message) {
        String sessionId = userSessions.get(userId.toString());
        if (sessionId != null) {
            WebSocketSession session = sessions.get(sessionId);
            if (session != null && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
                } catch (IOException e) {
                    log.error("Error sending message to user {}: {}", userId, e.getMessage());
                }
            }
        }
    }

    public void sendMessageToRoom(Long roomId, ChatMessageDto.WebSocketMessage message) {
        Set<String> roomSessionIds = roomSessions.get(roomId);
        if (roomSessionIds != null) {
            String messageJson;
            try {
                messageJson = objectMapper.writeValueAsString(message);
            } catch (Exception e) {
                log.error("Error serializing message: {}", e.getMessage());
                return;
            }

            roomSessionIds.forEach(sessionId -> {
                WebSocketSession session = sessions.get(sessionId);
                if (session != null && session.isOpen()) {
                    try {
                        session.sendMessage(new TextMessage(messageJson));
                    } catch (IOException e) {
                        log.error("Error sending message to session {}: {}", sessionId, e.getMessage());
                    }
                }
            });
            
            log.info("Message sent to {} sessions in room {}", roomSessionIds.size(), roomId);
        } else {
            log.warn("No sessions found for room {}", roomId);
        }
    }

    // 디버깅용 메소드들
    public Map<Long, Set<String>> getRoomSessions() {
        return new java.util.HashMap<>(roomSessions);
    }

    public Map<String, WebSocketSession> getSessions() {
        return new java.util.HashMap<>(sessions);
    }

    public Map<String, String> getUserSessions() {
        return new java.util.HashMap<>(userSessions);
    }
}