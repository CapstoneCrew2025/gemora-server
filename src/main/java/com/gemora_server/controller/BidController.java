package com.gemora_server.controller;

import com.gemora_server.dto.BidRequest;
import com.gemora_server.dto.BidResponse;
import com.gemora_server.entity.Bid;
import com.gemora_server.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "*")
public class BidController {

    @Autowired
    private BidService bidService;

    @PostMapping("/place")
    public BidResponse placeBid(@RequestBody BidRequest request) {
        return bidService.placeBid(request);
    }

    @GetMapping("/gem/{gemId}")
    public List<BidResponse> getBids(@PathVariable Long gemId) {
        return bidService.getBidsForGem(gemId);
    }



}
