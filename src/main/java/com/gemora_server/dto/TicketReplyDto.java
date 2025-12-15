package com.gemora_server.dto;

import com.gemora_server.enums.TicketStatus;
import lombok.Data;

@Data
public class TicketReplyDto {
    private String adminReply;
    private TicketStatus status;
}
