package com.gemora_server.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class InboxItemDto {

    private String roomId;
    private Long otherUserId;
    private String otherUserName;
    private Long gemId;
    private String lastMessage;
    private LocalDateTime lastSentAt;
    private Long unreadCount;


}
