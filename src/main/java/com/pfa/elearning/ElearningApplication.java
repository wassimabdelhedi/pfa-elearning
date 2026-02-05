package com.pfa.elearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the E-Learning Platform application.
 * 
 * This Spring Boot application provides a REST API for:
 * - User management (Admin, Teacher, Learner roles)
 * - Course management (Courses, Chapters, Contents)
 * - Enrollment and progress tracking
 * - Recommendation engine preparation (interfaces ready for future AI integration)
 * 
 * @author PFA Team
 * @version 1.0.0
 */
@SpringBootApplication
public class ElearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElearningApplication.class, args);
    }
}
