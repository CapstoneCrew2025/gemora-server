package com.gemora_server.service;


import com.gemora_server.dto.BidRequest;
import com.gemora_server.dto.BidResponse;

import java.util.List;

public interface BidService {
    BidResponse placeBid(BidRequest request , Long userId);

    List<BidResponse> getBidsForGem(Long gemId);
}
