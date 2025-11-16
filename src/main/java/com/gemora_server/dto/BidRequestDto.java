package com.gemora_server.dto;

import lombok.Data;

@Data
public class BidRequestDto {
    private Long gemId;
    private Long userId;
    private Double amount;
}
