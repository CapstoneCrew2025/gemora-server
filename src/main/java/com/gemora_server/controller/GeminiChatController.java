package com.gemora_server.controller;

import com.gemora_server.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class GeminiChatController {

    private final GeminiChatService geminiChatService;

    @PostMapping("/ask")
    public ResponseEntity<?> askGemini(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        String response = geminiChatService.askGemini(userMessage);
        return ResponseEntity.ok(response);
    }



}
