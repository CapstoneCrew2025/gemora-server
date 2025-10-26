package com.gemora_server.repo;

import com.gemora_server.entity.Certificate;
import com.gemora_server.entity.Gem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificateRepo extends JpaRepository<Certificate, Long> {
    List<Certificate> findByGem(Gem gem);
}
