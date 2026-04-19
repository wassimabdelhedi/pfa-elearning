package com.pfa.elearning.service;

import com.pfa.elearning.model.Role;
import com.pfa.elearning.model.User;
import com.pfa.elearning.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailScheduler {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CourseService courseService;

    // Run every day at 10 AM
    @Scheduled(cron = "0 0 10 * * ?")
    @Transactional
    public void checkInactivity() {
        log.info("Running daily inactivity check...");
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);

        // Find students who haven't logged in for 7 days
        List<User> students = userRepository.findByRole(Role.STUDENT);
        
        for (User student : students) {
            boolean isInactive = false;
            
            if (student.getLastLoginDate() != null && student.getLastLoginDate().isBefore(sevenDaysAgo)) {
                isInactive = true;
            } else if (student.getLastLoginDate() == null && student.getCreatedAt() != null && student.getCreatedAt().isBefore(sevenDaysAgo)) {
                // If they never logged in since account creation 7 days ago
                isInactive = true;
            }

            if (isInactive) {
                // Check if we already sent them an email recently (to avoid spamming them every day after 7 days)
                if (student.getLastInactivityEmailSent() == null || student.getLastInactivityEmailSent().isBefore(sevenDaysAgo)) {
                    log.info("Sending inactivity email to {}", student.getEmail());
                    emailService.sendInactivityEmail(student);
                    student.setLastInactivityEmailSent(LocalDateTime.now());
                    userRepository.save(student);
                }
            }
        }
    }

    // Run every Sunday at 10 AM
    @Scheduled(cron = "0 0 10 ? * SUN")
    public void sendWeeklySuggestions() {
        log.info("Running weekly course suggestions...");
        List<User> students = userRepository.findByRole(Role.STUDENT);
        
        for (User student : students) {
            try {
                // Fetch personalized courses using the existing recommendation logic
                var suggestions = courseService.getPersonalizedCourses(student);
                if (suggestions != null && !suggestions.isEmpty()) {
                    emailService.sendCourseSuggestionEmail(student, suggestions);
                }
            } catch (Exception e) {
                log.error("Failed to send weekly suggestions to {}", student.getEmail(), e);
            }
        }
    }
}
