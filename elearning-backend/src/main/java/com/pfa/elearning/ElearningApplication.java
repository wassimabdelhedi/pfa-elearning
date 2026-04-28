package com.pfa.elearning;

import com.pfa.elearning.model.Role;
import com.pfa.elearning.model.User;
import com.pfa.elearning.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class ElearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElearningApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Schema updates
            try {
                jdbcTemplate.execute("ALTER TABLE quiz_results ADD COLUMN IF NOT EXISTS failed BOOLEAN DEFAULT FALSE");
                jdbcTemplate.execute("ALTER TABLE quiz_results ADD COLUMN IF NOT EXISTS weak_topics TEXT");
                jdbcTemplate.execute("ALTER TABLE quiz_questions ADD COLUMN IF NOT EXISTS topic VARCHAR(100)");
                System.out.println("Database schema manually updated successfully.");
            } catch (Exception e) {
                System.err.println("Manual database update skipped or failed: " + e.getMessage());
            }

            // Ensure Admin exists
            String adminEmail = "admin@learnagent.com";
            if (!userRepository.existsByEmail(adminEmail)) {
                User admin = User.builder()
                        .firstName("Admin")
                        .lastName("System")
                        .email(adminEmail)
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .active(true)
                        .build();
                userRepository.save(admin);
                System.out.println(">>> Initial Administrator created: " + adminEmail);
            }
        };
    }
}
