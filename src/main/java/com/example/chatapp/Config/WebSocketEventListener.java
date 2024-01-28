package com.example.chatapp.Config;

import com.example.chatapp.chat.ChatMessage;
import com.example.chatapp.chat.MessageType;
import com.example.chatapp.entity.ChatMessageEntity;
import com.example.chatapp.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messagingTemplate;
    ChatMessageRepository chatMessageRepository ;
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        if (username != null) {
            log.info("user disconnected: {}", username);
            var chatMessage = ChatMessage.builder()
                    .type(MessageType.LEAVE)
                    .sender(username)
                    .build();
            messagingTemplate.convertAndSend("/topic/public", chatMessage);
        }
    }
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        if (headerAccessor.getSessionAttributes() != null) {
            String username = (String) headerAccessor.getSessionAttributes().get("username");
            if (username != null) {
                log.info("user connected: {}", username);
                var chatMessage = ChatMessage.builder()
                        .type(MessageType.JOIN)
                        .sender(username)
                        .build();
                messagingTemplate.convertAndSend("/topic/public", chatMessage);
            }
        }
    }


    private ChatMessage convertToChatMessage(ChatMessageEntity messageEntity) {
        return ChatMessage.builder()
                .type(MessageType.CHAT)
                .content(messageEntity.getContent())
                .sender(messageEntity.getSender())
                .build();
    }

}
