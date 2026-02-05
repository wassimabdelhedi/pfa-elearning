package tn.enis.pfa.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "recommendations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecommendationType type;

    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "module_id")
    private Long moduleId;

    @Column(length = 500)
    private String reason;

    private Double score;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    public enum RecommendationType {
        COURSE,
        CONTENT,
        REVISION
    }
}
