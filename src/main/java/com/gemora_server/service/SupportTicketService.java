package com.gemora_server.service;

import com.gemora_server.dto.CreateTicketDto;
import com.gemora_server.dto.TicketReplyDto;
import com.gemora_server.dto.TicketResponseDto;

import java.util.List;

public interface SupportTicketService {

    void createTicket(String token, CreateTicketDto dto);

    List<TicketResponseDto> getMyTickets(String token);

    List<TicketResponseDto> getAllTickets();

    void replyToTicket(Long ticketId, TicketReplyDto dto);
}
