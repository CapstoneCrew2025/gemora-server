package com.gemora_server.repo;

import com.gemora_server.entity.Gem;
import com.gemora_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gemora_server.enums.GemStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface GemRepo extends JpaRepository<Gem,Long> {
    List<Gem> findBySeller(User seller);
    List<Gem> findByStatus(GemStatus status);
    List<Gem> findByStatusAndAuctionEndTimeBefore(GemStatus status, LocalDateTime time);
}
