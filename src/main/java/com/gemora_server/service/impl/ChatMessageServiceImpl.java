package com.gemora_server.service.impl;

import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import com.gemora_server.entity.ChatMessage;
import com.gemora_server.enums.ChatMessageStatus;
import com.gemora_server.repo.ChatMessageRepo;
import com.gemora_server.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepo chatMessageRepository;

    public ChatMessageResponseDto saveMessage(ChatMessageRequestDto request) {
        String roomId = generateRoomId(request.getSenderId(), request.getReceiverId());
        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(request.getSenderId())
                .receiverId(request.getReceiverId())
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .status(ChatMessageStatus.SENT)
                .roomId(roomId)
                .build();

        ChatMessage saved = chatMessageRepository.save(chatMessage);

        return toResponse(saved);
    }

    public List<ChatMessageResponseDto> getChatHistory(Long user1Id, Long user2Id) {
        String roomId = generateRoomId(user1Id, user2Id);
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ChatMessageResponseDto toResponse(ChatMessage msg) {
        return ChatMessageResponseDto.builder()
                .id(msg.getId())
                .senderId(msg.getSenderId())
                .receiverId(msg.getReceiverId())
                .content(msg.getContent())
                .sentAt(msg.getSentAt())
                .status(msg.getStatus())
                .roomId(msg.getRoomId())
                .build();
    }

    public String generateRoomId(Long user1, Long user2) {
        if (user1 < user2) {
            return user1 + "_" + user2;
        } else {
            return user2 + "_" + user1;
        }
    }


}
