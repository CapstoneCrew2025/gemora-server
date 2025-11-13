package com.gemora_server.service;

import com.gemora_server.entity.Bid;
import java.util.List;

public interface BidService {
    Bid placeBid(Long gemId, Long userId, Double amount);
    List<Bid> getBidsForGem(Long gemId);
}
