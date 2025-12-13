package com.gemora_server.service;

import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import com.gemora_server.dto.InboxItemDto;

import java.util.List;

public interface ChatMessageService {

    ChatMessageResponseDto saveMessage(ChatMessageRequestDto request,Long senderId);

    List<ChatMessageResponseDto> getChatHistory(Long user1Id, Long user2Id,Long gemId);

    String generateRoomId(Long user1, Long user2,Long gemId );

    List<InboxItemDto> getInbox(Long userId);

    void markAsRead(Long userId, Long otherUserId, Long gemId);

}
