package com.gemora_server.repo;

import com.gemora_server.entity.Bid;
import com.gemora_server.entity.Gem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BidRepository extends JpaRepository<Bid,Long> {
    List<Bid> findByGemOrderByAmountDesc(Gem gem);
}
