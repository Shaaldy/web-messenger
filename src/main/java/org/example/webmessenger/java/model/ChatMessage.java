package org.example.webmessenger.java.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String fromLogin;
    private String toLogin;
    private String content;
    private String fromAvatar;
    private MessageType type;
}
