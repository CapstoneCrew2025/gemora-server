package com.gemora_server.dto;

import lombok.Data;

@Data
public class ChatMessageRequestDto {

    private Long senderId;
    private Long receiverId;
    private String content;

}
