package com.gemora_server.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuctionTimeResponseDto {

    private Long gemId;
    private Long remainingDays;
    private Long remainingHours;
    private Long remainingMinutes;
    private boolean expired;
}
