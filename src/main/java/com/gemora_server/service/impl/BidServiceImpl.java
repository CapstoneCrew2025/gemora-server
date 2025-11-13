package com.gemora_server.service.impl;

import com.gemora_server.entity.Bid;
import com.gemora_server.entity.Gem;
import com.gemora_server.entity.User;
import com.gemora_server.enums.ListingType;
import com.gemora_server.repo.BidRepo;
import com.gemora_server.repo.GemRepo;
import com.gemora_server.repo.UserRepo;
import com.gemora_server.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BidServiceImpl implements BidService {

    @Autowired
    private BidRepo bidRepository;

    @Autowired
    private GemRepo gemRepository;

    @Autowired
    private UserRepo userRepository;

    @Override
    public Bid placeBid(Long gemId, Long userId, Double amount) {

        Gem gem = gemRepository.findById(gemId)
                .orElseThrow(() -> new RuntimeException("Gem not found"));

        if (gem.getListingType() != ListingType.AUCTION) {
            throw new RuntimeException("This gem is not available for auction");
        }

        if (gem.getAuctionEndTime() != null &&
                gem.getAuctionEndTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Auction has ended");
        }

        // Starting price = gem.price (BigDecimal)
        double startingPrice = gem.getPrice().doubleValue();

        Double currentHighest = (gem.getCurrentHighestBid() != null)
                ? gem.getCurrentHighestBid()
                : startingPrice;

        if (amount <= currentHighest) {
            throw new RuntimeException("Your bid must be higher than the current highest bid");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bid bid = Bid.builder()
                .gem(gem)
                .bidder(user)
                .amount(BigDecimal.valueOf(amount))
                .placedAt(LocalDateTime.now())
                .build();
        bidRepository.save(bid);

        gem.setCurrentHighestBid(amount);
        gemRepository.save(gem);

        return bid;
    }

    @Override
    public List<Bid> getBidsForGem(Long gemId) {

        Gem gem = gemRepository.findById(gemId)
                .orElseThrow(() -> new RuntimeException("Gem not found"));

        return bidRepository.findByGemOrderByAmountDesc(gem);
    }


}
