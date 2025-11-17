package com.gemora_server.dto;

import com.gemora_server.enums.ChatMessageStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponseDto {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private ChatMessageStatus status;
    private String roomId;


}
