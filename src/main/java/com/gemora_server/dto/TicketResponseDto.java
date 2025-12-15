package com.gemora_server.dto;

import com.gemora_server.enums.TicketPriority;
import com.gemora_server.enums.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponseDto {
    private Long id;
    private String title;
    private String description;
    private String adminReply;
    private TicketStatus status;
    private TicketPriority priority;
    private LocalDateTime createdAt;
}
