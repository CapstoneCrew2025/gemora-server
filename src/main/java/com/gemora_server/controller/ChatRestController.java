package com.gemora_server.controller;

import com.gemora_server.dto.ChatHistoryRequestDto;
import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import com.gemora_server.service.ChatMessageService;
import com.gemora_server.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatMessageService chatMessageService;
    private final JwtUtil jwtUtil;

    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponseDto> testSendMessage(@RequestBody ChatMessageRequestDto request, HttpServletRequest httpRequest
    ) {

        String authHeader = httpRequest.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        Long senderId = jwtUtil.extractUserId(token);

        ChatMessageResponseDto response = chatMessageService.saveMessage(request, senderId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/history")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatHistory(
            @RequestBody ChatHistoryRequestDto request,
            HttpServletRequest httpRequest
    ) {
        {
            String authHeader = httpRequest.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Missing or invalid Authorization header");
            }

            String token = authHeader.substring(7);
            Long buyerId = jwtUtil.extractUserId(token);
            Long sellerId = request.getSellerId();

            if (sellerId == null) {
                throw new RuntimeException("sellerId is required.");
            }

            List<ChatMessageResponseDto> history =
                    chatMessageService.getChatHistory(buyerId, sellerId,request.getGemId());

            return ResponseEntity.ok(history);
        }


    }

}
