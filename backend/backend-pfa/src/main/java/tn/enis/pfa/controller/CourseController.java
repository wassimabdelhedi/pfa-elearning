package tn.enis.pfa.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import tn.enis.pfa.entity.Course;
import tn.enis.pfa.entity.Progress;
import tn.enis.pfa.service.CourseService;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 1. Create Course (Only TEACHER)
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public ResponseEntity<Course> createCourse(@RequestBody Course course, Principal principal) {
        return ResponseEntity.ok(courseService.saveCourse(course, principal.getName()));
    }

    // 2. Get All Courses (Public or Authenticated)
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // 3. Enroll (Only LEARNER)
    @PostMapping("/{id}/enroll")
    @PreAuthorize("hasAuthority('ROLE_LEARNER')")
    public ResponseEntity<?> enroll(@PathVariable Long id, Principal principal) {
        courseService.enroll(id, principal.getName());
        return ResponseEntity.ok(java.util.Collections.singletonMap("message", "Enrolled successfully"));
    }

    // 4. Get My Progress (Only LEARNER)
    @GetMapping("/progress")
    @PreAuthorize("hasAuthority('ROLE_LEARNER')")
    public List<Progress> getMyProgress(Principal principal) {
        return courseService.getStudentProgress(principal.getName());
    }
}
