package com.pfa.elearning.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private double relevanceScore;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(updatable = false)
    private LocalDateTime recommendedAt;

    @Builder.Default
    private boolean clicked = false;

    @PrePersist
    protected void onCreate() {
        this.recommendedAt = LocalDateTime.now();
    }
}
