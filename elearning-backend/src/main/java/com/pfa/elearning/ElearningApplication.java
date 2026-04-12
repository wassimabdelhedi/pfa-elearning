package com.pfa.elearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.pfa.elearning.model.Role;
import com.pfa.elearning.model.User;
import com.pfa.elearning.repository.UserRepository;

@SpringBootApplication
public class ElearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElearningApplication.class, args);
    }

    @Bean
    public org.springframework.boot.CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@learnagent.com").isEmpty()) {
                User admin = User.builder()
                        .firstName("Super")
                        .lastName("Admin")
                        .email("admin@learnagent.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .active(true)
                        .build();
                userRepository.save(admin);
                System.out.println("====== SYSTEM ADMIN CREATED ======");
                System.out.println("Email: admin@learnagent.com");
                System.out.println("Password: admin123");
                System.out.println("==================================");
            }
        };
    }
}
