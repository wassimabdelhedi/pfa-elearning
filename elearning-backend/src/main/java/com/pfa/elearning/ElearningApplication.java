package com.pfa.elearning;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class ElearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElearningApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("ALTER TABLE quiz_results ADD COLUMN IF NOT EXISTS failed BOOLEAN DEFAULT FALSE");
                jdbcTemplate.execute("ALTER TABLE quiz_results ADD COLUMN IF NOT EXISTS weak_topics TEXT");
                jdbcTemplate.execute("ALTER TABLE quiz_questions ADD COLUMN IF NOT EXISTS topic VARCHAR(100)");
                System.out.println("Database schema manually updated successfully.");
            } catch (Exception e) {
                System.err.println("Manual database update skipped or failed: " + e.getMessage());
            }
        };
    }
}
