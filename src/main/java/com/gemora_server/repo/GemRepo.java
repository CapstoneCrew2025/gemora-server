package com.gemora_server.repo;

import com.gemora_server.entity.Gem;
import com.gemora_server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.gemora_server.enums.GemStatus;
import java.util.List;

public interface GemRepo extends JpaRepository<Gem,Long> {
    List<Gem> findBySeller(User seller);
    List<Gem> findByStatus(GemStatus status);
}
