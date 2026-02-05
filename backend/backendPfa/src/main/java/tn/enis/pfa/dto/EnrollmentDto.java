package tn.enis.pfa.dto;

import lombok.Data;
import tn.enis.pfa.entity.Enrollment;

import java.time.Instant;

@Data
public class EnrollmentDto {

    private Long id;
    private Long userId;
    private Long courseId;
    private String courseTitle;
    private Instant enrolledAt;
    private Boolean completed;

    public static EnrollmentDto from(Enrollment e) {
        EnrollmentDto dto = new EnrollmentDto();
        dto.setId(e.getId());
        dto.setUserId(e.getUser().getId());
        dto.setCourseId(e.getCourse().getId());
        dto.setCourseTitle(e.getCourse().getTitle());
        dto.setEnrolledAt(e.getEnrolledAt());
        dto.setCompleted(e.getCompleted());
        return dto;
    }
}
