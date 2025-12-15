package com.gemora_server.repo;

import com.gemora_server.entity.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepo extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> findByUserId(Long userId);

}
