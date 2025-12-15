package com.gemora_server.service.impl;

import com.gemora_server.dto.CreateTicketDto;
import com.gemora_server.dto.TicketReplyDto;
import com.gemora_server.dto.TicketResponseDto;
import com.gemora_server.entity.SupportTicket;
import com.gemora_server.enums.TicketStatus;
import com.gemora_server.repo.SupportTicketRepo;
import com.gemora_server.service.SupportTicketService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepo ticketRepository;
    private final JwtUtil jwtUtil;

    @Override
    public void createTicket(String token, CreateTicketDto dto) {

        Long userId = jwtUtil.extractUserId(token);

        SupportTicket ticket = SupportTicket.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .priority(dto.getPriority())
                .status(TicketStatus.OPEN)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        ticketRepository.save(ticket);
    }

    @Override
    public List<TicketResponseDto> getMyTickets(String token) {

        Long userId = jwtUtil.extractUserId(token);

        return ticketRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public List<TicketResponseDto> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public void replyToTicket(Long ticketId, TicketReplyDto dto) {

        SupportTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setAdminReply(dto.getAdminReply());
        ticket.setStatus(dto.getStatus());
        ticket.setUpdatedAt(LocalDateTime.now());

        ticketRepository.save(ticket);
    }

    private TicketResponseDto mapToDto(SupportTicket ticket) {
        return TicketResponseDto.builder()
                .id(ticket.getId())
                .title(ticket.getTitle())
                .description(ticket.getDescription())
                .adminReply(ticket.getAdminReply())
                .status(ticket.getStatus())
                .priority(ticket.getPriority())
                .createdAt(ticket.getCreatedAt())
                .build();
    }



}
