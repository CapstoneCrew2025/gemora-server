package com.gemora_server.controller;

import com.gemora_server.dto.CreateTicketDto;
import com.gemora_server.dto.TicketReplyDto;
import com.gemora_server.dto.TicketResponseDto;
import com.gemora_server.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SupportTicketController {

    private final SupportTicketService ticketService;

    // User creates ticket
    @PostMapping
    public ResponseEntity<String> createTicket(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateTicketDto dto) {

        ticketService.createTicket(token, dto);
        return ResponseEntity.ok("Ticket created successfully");
    }

    // User views own tickets
    @GetMapping("/my")
    public ResponseEntity<List<TicketResponseDto>> getMyTickets(
            @RequestHeader("Authorization") String token) {

        return ResponseEntity.ok(ticketService.getMyTickets(token));
    }

    // Admin views all tickets
    @GetMapping("/admin")
    public ResponseEntity<List<TicketResponseDto>> getAllTickets() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // Admin replies & updates status
    @PutMapping("/admin/{ticketId}/reply")
    public ResponseEntity<String> replyToTicket(
            @PathVariable Long ticketId,
            @RequestBody TicketReplyDto dto) {

        ticketService.replyToTicket(ticketId, dto);
        return ResponseEntity.ok("Reply sent successfully");
    }
}
