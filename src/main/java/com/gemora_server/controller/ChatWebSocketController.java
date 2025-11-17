package com.gemora_server.controller;


import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import com.gemora_server.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequestDto request) {

        // Save message to DB
        ChatMessageResponseDto response = chatMessageService.saveMessage(request);

        // Topic for this chat room
        String destination = "/topic/chat." + response.getRoomId();

        // Send to all subscribers of this room
        messagingTemplate.convertAndSend(destination, response);
    }

}
