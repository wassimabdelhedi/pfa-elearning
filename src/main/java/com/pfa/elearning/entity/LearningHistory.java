package com.pfa.elearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * LearningHistory entity for tracking user learning activities.
 * 
 * This entity is prepared for future recommendation engine integration.
 * It stores historical data that can be used by AI algorithms to:
 * - Analyze learning patterns
 * - Generate personalized recommendations
 * - Track user engagement over time
 */
@Entity
@Table(name = "learning_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String activityType;  // e.g., "VIEW", "COMPLETE", "QUIZ_ATTEMPT"

    @Column(columnDefinition = "TEXT")
    private String activityData;  // JSON data for additional activity details

    @Column(nullable = false)
    private LocalDateTime activityTimestamp;

    @Column
    private Integer durationSeconds;  // Time spent on the activity

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @PrePersist
    protected void onCreate() {
        if (activityTimestamp == null) {
            activityTimestamp = LocalDateTime.now();
        }
    }
}
