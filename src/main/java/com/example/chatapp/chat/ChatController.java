package com.example.chatapp.chat;

import com.example.chatapp.entity.ChatMessageEntity;
import com.example.chatapp.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        // Save the message to the database
        saveMessageToDatabase(chatMessage);

        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage) {
        // Save the join message to the database
        saveMessageToDatabase(chatMessage);

        return chatMessage;
    }

    private void saveMessageToDatabase(ChatMessage chatMessage) {
        ChatMessageEntity messageEntity = new ChatMessageEntity();
        messageEntity.setContent(chatMessage.getContent());
        messageEntity.setSender(chatMessage.getSender());
        messageEntity.setTimestamp(LocalDateTime.now());

        chatMessageRepository.save(messageEntity);
    }
}