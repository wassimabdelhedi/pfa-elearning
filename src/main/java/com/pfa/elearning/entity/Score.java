package com.pfa.elearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Score entity for storing user assessment/quiz scores.
 * 
 * This entity is prepared for future recommendation engine integration.
 * Scores can be used to:
 * - Evaluate learner proficiency
 * - Adjust difficulty of recommended content
 * - Identify knowledge gaps
 */
@Entity
@Table(name = "scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String assessmentType;  // e.g., "QUIZ", "EXAM", "EXERCISE"

    @Column
    private String assessmentId;  // Reference to specific assessment

    @Column(nullable = false)
    private Double scoreValue;  // Score achieved (0-100)

    @Column
    private Double maxScore;  // Maximum possible score

    @Column
    private Integer attemptNumber;  // Which attempt this was

    @Column(nullable = false)
    private LocalDateTime attemptedAt;

    @Column
    private Integer timeTakenSeconds;  // Time taken to complete

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @PrePersist
    protected void onCreate() {
        if (attemptedAt == null) {
            attemptedAt = LocalDateTime.now();
        }
    }
}
