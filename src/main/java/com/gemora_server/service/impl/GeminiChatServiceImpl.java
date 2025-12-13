package com.gemora_server.service.impl;

import com.gemora_server.service.GeminiChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

        return geminiClient.post()
                .uri(apiUrl + "?key=" + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(err -> Mono.just("Error: " + err.getMessage()))
                .block();
    }


}
