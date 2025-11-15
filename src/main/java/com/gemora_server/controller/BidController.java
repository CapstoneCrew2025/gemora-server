package com.gemora_server.controller;

import com.gemora_server.dto.BidRequest;
import com.gemora_server.dto.BidResponse;
import com.gemora_server.service.BidService;
import com.gemora_server.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/bids")
@CrossOrigin(origins = "*")
public class BidController {

    private final BidService bidService;
    private  final JwtUtil jwtUtil;

    @PostMapping("/place")
    public BidResponse placeBid(@RequestBody BidRequest request,@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Long userId = jwtUtil.extractUserId(token);
        return bidService.placeBid(request,userId);
    }

    @GetMapping("/gem/{gemId}")
    public List<BidResponse> getBids(@PathVariable Long gemId) {
        return bidService.getBidsForGem(gemId);
    }



}
