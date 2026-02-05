package tn.enis.pfa.dto;

import lombok.Data;
import tn.enis.pfa.entity.Progress;

import java.time.Instant;

@Data
public class ProgressDto {

    private Long id;
    private Long enrollmentId;
    private Long contentId;
    private Long exerciseId;
    private Boolean completed;
    private Double score;
    private Instant completedAt;

    public static ProgressDto from(Progress p) {
        ProgressDto dto = new ProgressDto();
        dto.setId(p.getId());
        dto.setEnrollmentId(p.getEnrollment().getId());
        dto.setContentId(p.getContent() != null ? p.getContent().getId() : null);
        dto.setExerciseId(p.getExercise() != null ? p.getExercise().getId() : null);
        dto.setCompleted(p.getCompleted());
        dto.setScore(p.getScore());
        dto.setCompletedAt(p.getCompletedAt());
        return dto;
    }
}
