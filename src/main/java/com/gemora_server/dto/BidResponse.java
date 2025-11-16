package com.gemora_server.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class BidResponse {

    private Long bidId;
    private Long gemId;
    private Long bidderId;
    private BigDecimal amount;
    private LocalDateTime placedAt;
    private Long remainingSeconds;

}
