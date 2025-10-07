package com.gemora_server.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gemora_server.entity.User;
import java.util.Optional;


public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);


}
