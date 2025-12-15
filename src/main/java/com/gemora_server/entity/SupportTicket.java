package com.gemora_server.entity;

import com.gemora_server.enums.TicketPriority;
import com.gemora_server.enums.TicketStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "support_tickets")
public class SupportTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ticket basic info
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // User who created the ticket
    private Long userId;

    // Admin reply
    @Column(columnDefinition = "TEXT")
    private String adminReply;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    @Enumerated(EnumType.STRING)
    private TicketPriority priority;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
