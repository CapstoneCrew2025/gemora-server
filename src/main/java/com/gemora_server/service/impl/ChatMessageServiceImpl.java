package com.gemora_server.service.impl;

import com.gemora_server.dto.ChatMessageRequestDto;
import com.gemora_server.dto.ChatMessageResponseDto;
import com.gemora_server.dto.InboxItemDto;
import com.gemora_server.entity.ChatMessage;
import com.gemora_server.entity.Gem;
import com.gemora_server.enums.ChatMessageStatus;
import com.gemora_server.repo.ChatMessageRepo;
import com.gemora_server.repo.GemRepo;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.gemora_server.entity.User;


@Service
@RequiredArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    private final ChatMessageRepo chatMessageRepository;
    private final UserRepo userRepo;
    private final GemRepo gemRepo ;

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



    public List<InboxItemDto> getInbox(Long userId) {
        List<Object[]> rooms = chatMessageRepository.findRoomIdAndLastAtByUser(userId);

        List<InboxItemDto> inbox = new ArrayList<>();

        for (Object[] row : rooms) {
            String roomId = (String) row[0];

            ChatMessage lastMsg = chatMessageRepository.findTopByRoomIdOrderBySentAtDesc(roomId);
            if (lastMsg == null) continue;

            Long gemId = lastMsg.getGemId();

            String gemName = gemRepo.findById(gemId)
                    .map(Gem::getName) // use your correct gem name field
                    .orElse("Unknown Gem");

            Long otherUserId = lastMsg.getSenderId().equals(userId) ? lastMsg.getReceiverId() : lastMsg.getSenderId();

            String otherUserName = userRepo.findById(otherUserId)
                    .map(User::getName)
                    .orElse("Unknown");

            long unreadCount = 0L;
            try {
                unreadCount = chatMessageRepository.countByRoomIdAndReceiverIdAndStatus(roomId, userId, ChatMessageStatus.SENT);

            } catch (Exception e) {
                unreadCount = 0L;
            }
            InboxItemDto item = InboxItemDto.builder()
                    .roomId(roomId)
                    .otherUserId(otherUserId)
                    .gemName(gemName)
                    .gemId(gemId)
                    .lastMessage(lastMsg.getContent())
                    .lastSentAt(lastMsg.getSentAt())
                    .unreadCount(unreadCount)
                    .build();

            inbox.add(item);
        }

        return inbox;
    }


}
