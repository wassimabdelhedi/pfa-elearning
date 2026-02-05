package com.pfa.elearning.dto.enrollment;

import com.pfa.elearning.enums.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for enrollment response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;
    private Long userId;
    private Long courseId;
    private String courseTitle;
    private String courseThumbnail;
    private ProgressStatus status;
    private Double completionPercentage;
    private LocalDateTime enrolledAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}
