package com.knit.api.config;

import com.knit.api.handler.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 네이티브 WebSocket (앱용)
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .setAllowedOriginPatterns("*"); // 모든 도메인 허용 (개발용)
        
        // SockJS WebSocket (웹용)
        registry.addHandler(chatWebSocketHandler, "/ws/chat-sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // SockJS 지원 추가
    }
}