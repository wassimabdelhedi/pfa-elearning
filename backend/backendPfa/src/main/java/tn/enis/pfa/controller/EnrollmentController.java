package tn.enis.pfa.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enis.pfa.dto.EnrollmentDto;
import tn.enis.pfa.security.CurrentUserId;
import tn.enis.pfa.service.EnrollmentService;

import java.util.List;

@RestController
@RequestMapping("/enrollments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @GetMapping("/me")
    public ResponseEntity<List<EnrollmentDto>> myEnrollments(@CurrentUserId Long userId) {
        return ResponseEntity.ok(enrollmentService.findByUser(userId));
    }

    @PostMapping("/courses/{courseId}")
    public ResponseEntity<EnrollmentDto> enroll(@CurrentUserId Long userId,
                                                @PathVariable Long courseId) {
        return ResponseEntity.ok(enrollmentService.enroll(userId, courseId));
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkEnrolled(@CurrentUserId Long userId,
                                                 @RequestParam Long courseId) {
        return ResponseEntity.ok(enrollmentService.isEnrolled(userId, courseId));
    }
}
