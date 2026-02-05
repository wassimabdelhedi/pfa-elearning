package com.pfa.elearning.entity;

import com.pfa.elearning.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Enrollment entity representing a learner's enrollment in a course.
 * 
 * Tracks enrollment date, completion status, and overall progress.
 */
@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "course_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProgressStatus status = ProgressStatus.NOT_STARTED;

    @Column(nullable = false)
    private Double completionPercentage = 0.0;

    @Column(nullable = false, updatable = false)
    private LocalDateTime enrolledAt;

    @Column
    private LocalDateTime completedAt;

    @Column
    private LocalDateTime lastAccessedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Progress> progressRecords;

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
        lastAccessedAt = LocalDateTime.now();
    }
}
