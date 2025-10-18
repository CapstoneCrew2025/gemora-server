package com.gemora_server.config;

import com.gemora_server.entity.User;
import com.gemora_server.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepo userRepository;

    @Override
    public void run(String... args) {
        seedAdminUser();
    }

    private void seedAdminUser() {
        String adminEmail = "admin@gemora.com";

        if (userRepository.existsByEmail(adminEmail)) {
            System.out.println("✅ Admin user already exists.");
            return;
        }


        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode("admin123");


        User admin = User.builder()
                .name("System Admin")
                .email(adminEmail)
                .password(hashedPassword)
                .role("ADMIN")
                .build();

        userRepository.save(admin);
        System.out.println("🚀 Default admin account created: " + adminEmail);
    }
}
