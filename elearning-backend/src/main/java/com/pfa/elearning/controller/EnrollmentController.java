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

import java.util.LinkedHashMap;
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

        Map<String, Double> progressDetails = enrollmentService.calculateDetailedProgress(enrollment);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", enrollment.getId());
        result.put("courseId", enrollment.getCourse().getId());
        result.put("courseTitle", enrollment.getCourse().getTitle());
        result.put("enrolledAt", enrollment.getEnrolledAt().toString());
        result.put("progress", progressDetails.get("overallProgress"));
        result.put("chaptersProgress", progressDetails.get("chaptersProgress"));
        result.put("quizzesProgress", progressDetails.get("quizzesProgress"));
        result.put("exercisesProgress", progressDetails.get("exercisesProgress"));

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getMyEnrollments(Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        List<Enrollment> enrollments = enrollmentService.getStudentEnrollments(student.getId());

        List<Map<String, Object>> result = enrollments.stream().map(e -> {
            Map<String, Double> progressDetails = enrollmentService.calculateDetailedProgress(e);
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", e.getId());
            map.put("courseId", e.getCourse().getId());
            map.put("courseTitle", e.getCourse().getTitle());
            map.put("teacherName", e.getCourse().getTeacher().getFullName());
            map.put("enrolledAt", e.getEnrolledAt().toString());
            map.put("progress", progressDetails.get("overallProgress"));
            map.put("chaptersProgress", progressDetails.get("chaptersProgress"));
            map.put("quizzesProgress", progressDetails.get("quizzesProgress"));
            map.put("exercisesProgress", progressDetails.get("exercisesProgress"));
            map.put("completed", e.isCompleted());
            return map;
        }).collect(Collectors.toList());

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

    @PutMapping("/course/{courseId}/progress")
    public ResponseEntity<Map<String, Object>> updateProgressByCourse(
            @PathVariable Long courseId,
            @RequestBody Map<String, Double> body,
            Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        double progress = body.getOrDefault("progress", 0.0);
        Enrollment enrollment = enrollmentService.updateProgressByCourseId(courseId, progress, student);

        return ResponseEntity.ok(Map.of(
                "id", enrollment.getId(),
                "progress", enrollment.getProgressPercentage(),
                "completed", enrollment.isCompleted()
        ));
    }
}
