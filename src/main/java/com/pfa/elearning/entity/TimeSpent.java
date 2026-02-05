package com.pfa.elearning.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * TimeSpent entity for tracking time spent on specific content.
 * 
 * This entity is prepared for future recommendation engine integration.
 * Time tracking data can be used to:
 * - Understand content difficulty
 * - Optimize content length
 * - Personalize learning pace recommendations
 */
@Entity
@Table(name = "time_spent")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSpent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer durationSeconds;  // Time spent in seconds

    @Column(nullable = false)
    private LocalDateTime sessionStart;

    @Column
    private LocalDateTime sessionEnd;

    @Column
    private Boolean completed = false;  // Whether content was completed in this session

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @PrePersist
    protected void onCreate() {
        if (sessionStart == null) {
            sessionStart = LocalDateTime.now();
        }
    }
}
