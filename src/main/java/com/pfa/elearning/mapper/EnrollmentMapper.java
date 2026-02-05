package com.pfa.elearning.mapper;

import com.pfa.elearning.dto.enrollment.EnrollmentResponse;
import com.pfa.elearning.entity.Enrollment;
import org.springframework.stereotype.Component;

/**
 * Mapper for Enrollment entity to DTO conversions.
 */
@Component
public class EnrollmentMapper {

    public EnrollmentResponse toResponse(Enrollment enrollment) {
        if (enrollment == null) return null;

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .userId(enrollment.getUser() != null ? enrollment.getUser().getId() : null)
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .courseThumbnail(enrollment.getCourse() != null ? enrollment.getCourse().getThumbnailUrl() : null)
                .status(enrollment.getStatus())
                .completionPercentage(enrollment.getCompletionPercentage())
                .enrolledAt(enrollment.getEnrolledAt())
                .completedAt(enrollment.getCompletedAt())
                .lastAccessedAt(enrollment.getLastAccessedAt())
                .build();
    }
}
