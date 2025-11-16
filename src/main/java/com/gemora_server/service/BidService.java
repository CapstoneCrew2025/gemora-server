package com.gemora_server.service;


import com.gemora_server.dto.BidRequestDto;
import com.gemora_server.dto.BidResponseDto;

import java.util.List;

public interface BidService {
    BidResponseDto placeBid(BidRequestDto request , Long userId);

    List<BidResponseDto> getBidsForGem(Long gemId);
}
