package com.gemora_server.repo;

import com.gemora_server.entity.GemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GemImageRepo extends JpaRepository<GemImage, Long> {
}
