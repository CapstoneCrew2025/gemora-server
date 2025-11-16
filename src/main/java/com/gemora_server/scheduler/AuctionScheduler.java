package com.gemora_server.scheduler;

import com.gemora_server.entity.Gem;
import com.gemora_server.enums.GemStatus;
import com.gemora_server.repo.GemRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

    private final GemRepo gemRepo;

    @Scheduled(fixedRate = 3600000) // Runs every 1 hour
    public void checkExpiredAuctions() {

        LocalDateTime now = LocalDateTime.now();

        List<Gem> expiredAuctions = gemRepo.findByStatusAndAuctionEndTimeBefore(
                GemStatus.APPROVED, now
        );

        for (Gem gem : expiredAuctions) {

            gem.setStatus(GemStatus.PENDING);
            gem.setCurrentHighestBid(null);
            gem.setAuctionEndTime(null);
            gemRepo.save(gem);
        }
    }
}

