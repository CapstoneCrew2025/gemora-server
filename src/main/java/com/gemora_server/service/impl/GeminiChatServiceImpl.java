package com.gemora_server.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gemora_server.exception.BusinessException;
import com.gemora_server.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiChatServiceImpl implements GeminiChatService {


    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.system.instruction}")
    private String systemInstruction;

    private final WebClient geminiClient;

    @Override
    public String askGemini(String userMessage) {

        if (userMessage == null || userMessage.isBlank()) {
            throw new BusinessException("Message cannot be empty");
        }

        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", systemInstruction)
                                )
                        ),
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", userMessage)
                                )
                        )
                )
        );

        String rawResponse = geminiClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            // Parse Gemini response
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(rawResponse);

            String text = json
                    .path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText("");

            return text.isEmpty() ? "No response from model" : text;

        } catch (Exception e) {
            throw new BusinessException("Invalid response format from Gemini API");
        }
    }

}
