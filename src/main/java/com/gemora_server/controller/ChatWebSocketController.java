package com.gemora_server.controller;


import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import com.gemora_server.service.ChatMessageService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate messagingTemplate;
    private  final JwtUtil jwtUtil;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageRequestDto request,
                            @Header("Authorization") String authHeader) {


        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header in WebSocket frame");
        }

        String token = authHeader.substring(7);

        Long senderId = jwtUtil.extractUserId(token);
        // Save message to DB
        ChatMessageResponseDto response = chatMessageService.saveMessage(request, senderId);

        // Topic for this chat room
        String destination = "/topic/chat." + response.getRoomId();

        // Send to all subscribers of this room
        messagingTemplate.convertAndSend(destination, response);
    }

}
