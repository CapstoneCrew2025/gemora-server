package com.gemora_server.service;

import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import java.util.List;

public interface ChatMessageService {

    ChatMessageResponseDto saveMessage(ChatMessageRequestDto request);

    List<ChatMessageResponseDto> getChatHistory(Long user1Id, Long user2Id);

    String generateRoomId(Long user1, Long user2);

}
