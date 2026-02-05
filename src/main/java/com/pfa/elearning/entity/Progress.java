package com.pfa.elearning.entity;

import com.pfa.elearning.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Progress entity tracking a learner's progress on a specific chapter.
 * 
 * Used to calculate overall course completion percentage.
 */
@Entity
@Table(name = "progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"enrollment_id", "chapter_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Progress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(nullable = false)
    private Double completionPercentage = 0.0;

    @Column
    private LocalDateTime startedAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime lastAccessedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @PrePersist
    protected void onCreate() {
        if (startedAt == null && status == ProgressStatus.IN_PROGRESS) {
            startedAt = LocalDateTime.now();
        }
        lastAccessedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastAccessedAt = LocalDateTime.now();
        if (status == ProgressStatus.COMPLETED && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
}
