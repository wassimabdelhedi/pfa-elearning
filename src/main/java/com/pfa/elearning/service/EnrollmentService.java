package com.pfa.elearning.service;

import com.pfa.elearning.dto.enrollment.EnrollmentResponse;
import com.pfa.elearning.entity.Course;
import com.pfa.elearning.entity.Enrollment;
import com.pfa.elearning.entity.User;
import com.pfa.elearning.enums.ProgressStatus;
import com.pfa.elearning.exception.BadRequestException;
import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.mapper.EnrollmentMapper;
import com.pfa.elearning.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for enrollment management operations.
 * Handles learner enrollment in courses.
 */
@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentMapper enrollmentMapper;

    /**
     * Enroll a learner in a course.
     */
    @Transactional
    public EnrollmentResponse enrollInCourse(Long userId, Long courseId) {
        User user = userService.getUserEntityById(userId);
        Course course = courseService.getCourseEntity(courseId);

        if (!course.getPublished()) {
            throw new BadRequestException("Cannot enroll in unpublished course");
        }

        if (enrollmentRepository.existsByUserIdAndCourseId(userId, courseId)) {
            throw new BadRequestException("Already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .status(ProgressStatus.NOT_STARTED)
                .completionPercentage(0.0)
                .build();

        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    /**
     * Get enrollment by user and course.
     */
    public EnrollmentResponse getEnrollment(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
        return enrollmentMapper.toResponse(enrollment);
    }

    /**
     * Get enrollment entity.
     */
    public Enrollment getEnrollmentEntity(Long userId, Long courseId) {
        return enrollmentRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found"));
    }

    /**
     * Get all enrollments for a user.
     */
    public List<EnrollmentResponse> getUserEnrollments(Long userId) {
        return enrollmentRepository.findByUserIdOrderByLastAccessedAtDesc(userId).stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get in-progress enrollments for a user.
     */
    public List<EnrollmentResponse> getInProgressEnrollments(Long userId) {
        return enrollmentRepository.findInProgressByUserId(userId).stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get completed enrollments for a user.
     */
    public List<EnrollmentResponse> getCompletedEnrollments(Long userId) {
        return enrollmentRepository.findCompletedByUserId(userId).stream()
                .map(enrollmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update enrollment progress.
     */
    @Transactional
    public EnrollmentResponse updateEnrollmentProgress(Long userId, Long courseId, Double completionPercentage) {
        Enrollment enrollment = getEnrollmentEntity(userId, courseId);

        enrollment.setCompletionPercentage(completionPercentage);
        enrollment.setLastAccessedAt(LocalDateTime.now());

        if (completionPercentage >= 100.0) {
            enrollment.setStatus(ProgressStatus.COMPLETED);
            enrollment.setCompletedAt(LocalDateTime.now());
        } else if (completionPercentage > 0) {
            enrollment.setStatus(ProgressStatus.IN_PROGRESS);
        }

        return enrollmentMapper.toResponse(enrollmentRepository.save(enrollment));
    }

    /**
     * Update last accessed timestamp.
     */
    @Transactional
    public void updateLastAccessed(Long userId, Long courseId) {
        Enrollment enrollment = getEnrollmentEntity(userId, courseId);
        enrollment.setLastAccessedAt(LocalDateTime.now());
        
        if (enrollment.getStatus() == ProgressStatus.NOT_STARTED) {
            enrollment.setStatus(ProgressStatus.IN_PROGRESS);
        }
        
        enrollmentRepository.save(enrollment);
    }

    /**
     * Check if user is enrolled in course.
     */
    public boolean isEnrolled(Long userId, Long courseId) {
        return enrollmentRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    /**
     * Unenroll from course.
     */
    @Transactional
    public void unenroll(Long userId, Long courseId) {
        Enrollment enrollment = getEnrollmentEntity(userId, courseId);
        enrollmentRepository.delete(enrollment);
    }
}
