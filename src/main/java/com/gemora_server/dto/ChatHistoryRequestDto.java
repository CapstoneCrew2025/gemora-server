package com.gemora_server.dto;

import lombok.Data;

@Data
public class ChatHistoryRequestDto {
    private Long otherUserId;
    private Long gemId;

}
