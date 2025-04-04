package org.example.webmessenger.java.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.webmessenger.java.model.ChatMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        Principal principal = (Principal) session.getAttributes().get("user");
        System.out.println("[WebSocket] Connected: " + (principal != null ? principal.getName() : "anonymous"));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        Principal principal = (Principal) session.getAttributes().get("user");
        System.out.println("[WebSocket] Disconnected: " + (principal != null ? principal.getName() : "anonymous"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Principal principal = (Principal) session.getAttributes().get("user");
        if (principal == null) return;

        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        chatMessage.setFromUserId(principal.getName());

        String broadcast = objectMapper.writeValueAsString(chatMessage);
        for (WebSocketSession ws : sessions) {
            if (ws.isOpen()) {
                ws.sendMessage(new TextMessage(broadcast));
            }
        }
    }
}
