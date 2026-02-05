package com.pfa.elearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * UserPreference entity for storing user learning preferences.
 * 
 * This entity is prepared for future recommendation engine integration.
 * Preferences can be used to:
 * - Personalize content recommendations
 * - Adjust learning path suggestions
 * - Match users with appropriate courses
 */
@Entity
@Table(name = "user_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String preferredLanguage;  // e.g., "en", "fr", "ar"

    @Column
    private String preferredContentType;  // e.g., "VIDEO", "TEXT", "PDF"

    @Column
    private String preferredDifficulty;  // e.g., "Beginner", "Intermediate", "Advanced"

    @Column(columnDefinition = "TEXT")
    private String preferredCategories;  // JSON array of category preferences

    @Column
    private Integer dailyGoalMinutes;  // Daily learning goal in minutes

    @Column
    private String preferredLearningTime;  // e.g., "MORNING", "AFTERNOON", "EVENING"

    @Column(columnDefinition = "TEXT")
    private String additionalPreferences;  // JSON for extensible preferences

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
