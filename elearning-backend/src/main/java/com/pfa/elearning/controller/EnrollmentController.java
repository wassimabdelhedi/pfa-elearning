package com.pfa.elearning.controller;

import com.pfa.elearning.model.Enrollment;
import com.pfa.elearning.model.User;
import com.pfa.elearning.service.EnrollmentService;
import com.pfa.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final UserService userService;

    @PostMapping("/{courseId}")
    public ResponseEntity<Map<String, Object>> enroll(
            @PathVariable Long courseId,
            Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        Enrollment enrollment = enrollmentService.enrollStudent(student, courseId);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", enrollment.getId(),
                "courseId", enrollment.getCourse().getId(),
                "courseTitle", enrollment.getCourse().getTitle(),
                "enrolledAt", enrollment.getEnrolledAt().toString(),
                "progress", enrollment.getProgressPercentage()
        ));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getMyEnrollments(Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(student.getId());

        List<Map<String, Object>> result = enrollments.stream().map(e -> Map.<String, Object>of(
                "id", e.getId(),
                "courseId", e.getCourse().getId(),
                "courseTitle", e.getCourse().getTitle(),
                "teacherName", e.getCourse().getTeacher().getFullName(),
                "enrolledAt", e.getEnrolledAt().toString(),
                "progress", e.getProgressPercentage(),
                "completed", e.isCompleted()
        )).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Double> body,
            Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        double progress = body.getOrDefault("progress", 0.0);
        Enrollment enrollment = enrollmentService.updateProgress(id, progress, student);

        return ResponseEntity.ok(Map.of(
                "id", enrollment.getId(),
                "progress", enrollment.getProgressPercentage(),
                "completed", enrollment.isCompleted()
        ));
    }
}
