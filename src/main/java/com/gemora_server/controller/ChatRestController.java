package com.gemora_server.controller;

import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import com.gemora_server.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatMessageService chatMessageService;

    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponseDto> testSendMessage(@RequestBody ChatMessageRequestDto request) {
        ChatMessageResponseDto response = chatMessageService.saveMessage(request);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageResponseDto>> getChatHistory(
            @RequestParam Long user1Id,
            @RequestParam Long user2Id
    ) {
        List<ChatMessageResponseDto> history = chatMessageService.getChatHistory(user1Id, user2Id);
        return ResponseEntity.ok(history);
    }




}
