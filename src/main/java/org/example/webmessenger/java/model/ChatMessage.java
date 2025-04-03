package org.example.webmessenger.java.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {
    private String fromUserId;
    private String fromUserName;
    private String content;
    private MessageType messageType;
}
