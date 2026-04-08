package com.pfa.elearning.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chapter_progress", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"enrollment_id", "chapter_id"})
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChapterProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Builder.Default
    private double progressPercentage = 0.0;

    @Builder.Default
    private boolean completed = false;

    private LocalDateTime completedAt;
}
