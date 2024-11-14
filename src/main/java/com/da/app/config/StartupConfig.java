package com.da.app.config;

import com.da.app.domain.User;
import com.da.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class StartupConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Bean
    public ApplicationRunner initAdminAccount() {
        return args -> {
            String adminUsername = "admin";
            String adminPassword = "admin";
            String adminRole = "ADMIN";

            if (!userRepository.existsByUsername(adminUsername)) {
                User adminUser = new User();
                adminUser.setUsername(adminUsername);
                adminUser.setPassword(passwordEncoder.encode(adminPassword));
                adminUser.setRole(adminRole);

                userRepository.save(adminUser);
                System.out.println("Admin account created successfully.");
            } else {
                System.out.println("Admin account already exists.");
            }
        };
    }
}
