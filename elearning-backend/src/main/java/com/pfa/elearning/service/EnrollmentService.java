package com.pfa.elearning.service;

import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.model.Course;
import com.pfa.elearning.model.Enrollment;
import com.pfa.elearning.model.User;
import com.pfa.elearning.repository.CourseRepository;
import com.pfa.elearning.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Enrollment enrollStudent(User student, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalArgumentException("You are already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .progressPercentage(0.0)
                .completed(false)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getInProgressEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentIdAndCompletedFalse(studentId);
    }

    @Transactional
    public Enrollment updateProgress(Long enrollmentId, double progress, User student) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        if (!enrollment.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("You can only update your own enrollment");
        }

        enrollment.setProgressPercentage(Math.min(progress, 100.0));

        if (progress >= 100.0 && !enrollment.isCompleted()) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
        }

        return enrollmentRepository.save(enrollment);
    }

    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }
}
