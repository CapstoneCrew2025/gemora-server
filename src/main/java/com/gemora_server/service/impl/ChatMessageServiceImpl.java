package com.gemora_server.service.impl;

import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import com.gemora_server.entity.ChatMessage;
import com.gemora_server.enums.ChatMessageStatus;
import com.gemora_server.repo.ChatMessageRepo;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.gemora_server.entity.User;


@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepo chatMessageRepository;
    private final UserRepo userRepo;

    public ChatMessageResponseDto saveMessage(ChatMessageRequestDto request, Long senderId ) {

        String roomId = generateRoomId(senderId, request.getReceiverId(),request.getGemId());

        ChatMessage chatMessage = ChatMessage.builder()
                .senderId(senderId)
                .receiverId(request.getReceiverId())
                .gemId(request.getGemId())
                .content(request.getContent())
                .sentAt(LocalDateTime.now())
                .status(ChatMessageStatus.SENT)
                .roomId(roomId)
                .build();

        ChatMessage saved = chatMessageRepository.save(chatMessage);

        return toResponse(saved);
    }

    public List<ChatMessageResponseDto> getChatHistory(Long buyerId, Long sellerId,Long gemId) {

        // Generate unique roomId for the chat between buyer & seller
        String roomId = generateRoomId(buyerId, sellerId,gemId);

        // Fetch messages ordered by sent time ascending
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }


    private ChatMessageResponseDto toResponse(ChatMessage msg) {

        String senderName = userRepo.findById(msg.getSenderId())
                .map(User::getName)
                .orElse("Unknown");

        String receiverName = userRepo.findById(msg.getReceiverId())
                .map(User::getName)
                .orElse("Unknown");

        return ChatMessageResponseDto.builder()
                .id(msg.getId())
                .senderId(msg.getSenderId())
                .receiverId(msg.getReceiverId())
                .senderName(senderName)
                .receiverName(receiverName)
                .content(msg.getContent())
                .sentAt(msg.getSentAt())
                .status(msg.getStatus())
                .roomId(msg.getRoomId())
                .build();

    }

    public String generateRoomId(Long user1, Long user2, Long gemId) {
        Long u1 = Math.min(user1, user2);
        Long u2 = Math.max(user1, user2);
        return u1 + "_" + u2 + "_" + gemId;
    }



}
