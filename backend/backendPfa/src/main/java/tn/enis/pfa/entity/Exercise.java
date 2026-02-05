package tn.enis.pfa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "exercises")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExerciseType type;

    @Column(columnDefinition = "TEXT")
    private String correctAnswer;

    @Column(columnDefinition = "TEXT")
    private String optionsJson;

    @Column(nullable = false)
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private CourseModule module;

    public enum ExerciseType {
        QCM,
        OPEN
    }
}
