package com.gemora_server.service.impl;

import com.gemora_server.dto.BidRequest;
import com.gemora_server.dto.BidResponse;
import com.gemora_server.entity.Bid;
import com.gemora_server.entity.Gem;
import com.gemora_server.entity.User;
import com.gemora_server.enums.ListingType;
import com.gemora_server.repo.BidRepo;
import com.gemora_server.repo.GemRepo;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.BidService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BidServiceImpl implements BidService {

    private final BidRepo bidRepository;
    private final GemRepo gemRepository;
    private final UserRepo userRepository;

    @Transactional
    public BidResponse placeBid(BidRequest request , Long userId) {

        Gem gem = gemRepository.findById(request.getGemId())
                .orElseThrow(() -> new RuntimeException("Gem not found"));

        if (gem.getListingType() != ListingType.AUCTION) {
            throw new RuntimeException("This gem is not available for auction");
        }

        if (gem.getAuctionEndTime() != null &&
                gem.getAuctionEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Auction has ended");
        }

        double startingPrice = gem.getPrice().doubleValue();
        Double currentHighest = (gem.getCurrentHighestBid() != null)
                ? gem.getCurrentHighestBid()
                : startingPrice;

        if (request.getAmount() <= currentHighest) {
            throw new RuntimeException("Your bid must be higher than the current highest bid");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));


        Bid bid = Bid.builder()
                .gem(gem)
                .bidder(user)
                .amount(BigDecimal.valueOf(request.getAmount()))
                .placedAt(LocalDateTime.now())
                .build();

        bidRepository.save(bid);

        // Update highest bid in Gem
        gem.setCurrentHighestBid(request.getAmount());
        gemRepository.save(gem);

        return BidResponse.builder()
                .bidId(bid.getId())
                .gemId(gem.getId())
                .bidderId(user.getId())
                .amount(bid.getAmount())
                .placedAt(bid.getPlacedAt())
                .build();
    }

    @Override
    public List<BidResponse> getBidsForGem(Long gemId) {

        Gem gem = gemRepository.findById(gemId)
                .orElseThrow(() -> new RuntimeException("Gem not found"));

        List<Bid> bids = bidRepository.findByGemOrderByAmountDesc(gem);

        return bids.stream().map(bid -> BidResponse.builder()
                .bidId(bid.getId())
                .gemId(bid.getGem().getId())
                .bidderId(bid.getBidder().getId())
                .amount(bid.getAmount())
                .placedAt(bid.getPlacedAt())
                .build()
        ).collect(Collectors.toList());
    }
}
