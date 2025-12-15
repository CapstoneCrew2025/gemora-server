package com.gemora_server.dto;

import com.gemora_server.enums.TicketPriority;
import lombok.Data;

@Data
public class CreateTicketDto {
    private String title;
    private String description;
    private TicketPriority priority;
}