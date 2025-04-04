package org.example.webmessenger.java.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.webmessenger.java.model.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.security.Principal;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, WebSocketSession> loginToSession = new ConcurrentHashMap<>();
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        try {
            Principal principal = (Principal) session.getAttributes().get("user");


            if (principal instanceof OAuth2AuthenticationToken auth) {
                String login = auth.getPrincipal().getAttribute("email");
                String avatarUrl = auth.getPrincipal().getAttribute("picture");
                session.getAttributes().put("avatar", avatarUrl);
                log.info("✔ Подключён пользователь: " + login);
                loginToSession.put(login, session);
                session.getAttributes().put("login", login);
            } else {
                log.error("⚠ Principal не является OAuth2AuthenticationToken: " + principal);
            }

        } catch (Exception e) {
            log.error("❌ Ошибка в afterConnectionEstablished: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        Principal principal = (Principal) session.getAttributes().get("user");
        log.info("[WebSocket] Disconnected: " + (principal != null ? principal.getName() : "anonymous"));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            String fromLogin = (String) session.getAttributes().get("login");
            String fromAvatar = (String) session.getAttributes().get("avatar");
            log.info("Get avatar: " + fromAvatar);
            log.info("From login: " + fromLogin);
            chatMessage.setFromLogin(fromLogin);
            chatMessage.setFromAvatar(fromAvatar);
            String toLogin = chatMessage.getToLogin();
            WebSocketSession recipient = loginToSession.get(toLogin);

            if (recipient != null && recipient.isOpen()) {
                recipient.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
            } else {
                log.error("Получатель не найден или сессия закрыта: " + toLogin);
            }
        } catch (Exception e) {
            log.error("❌ Ошибка в handleTextMessage: " + e.getMessage());
            e.printStackTrace();
            throw e; // чтобы Spring закрыл корректно
        }
    }
}
