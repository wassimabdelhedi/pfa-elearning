package com.pfa.elearning.controller;

import com.pfa.elearning.dto.request.CourseRequest;
import com.pfa.elearning.dto.response.CourseResponse;
import com.pfa.elearning.model.Course;
import com.pfa.elearning.model.DifficultyLevel;
import com.pfa.elearning.model.User;
import com.pfa.elearning.service.CourseService;
import com.pfa.elearning.service.FileStorageService;
import com.pfa.elearning.service.SearchService;
import com.pfa.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final SearchService searchService;

    // ========== PUBLIC ENDPOINTS ==========

    @GetMapping
    public ResponseEntity<List<CourseResponse>> getAllPublishedCourses() {
        List<Course> courses = courseService.getPublishedCourses();
        return ResponseEntity.ok(courseService.toResponseList(courses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseResponse> getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return ResponseEntity.ok(courseService.toResponse(course));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CourseResponse>> getCoursesByCategory(@PathVariable Long categoryId) {
        List<Course> courses = courseService.getCoursesByCategory(categoryId);
        return ResponseEntity.ok(courseService.toResponseList(courses));
    }

    // ========== TEACHER ENDPOINTS ==========

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponse> createCourse(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "published", defaultValue = "false") boolean published,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());

        CourseRequest request = new CourseRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategoryId(categoryId);
        request.setLevel(level != null ? DifficultyLevel.valueOf(level) : DifficultyLevel.BEGINNER);
        request.setPublished(published);

        Course course = courseService.createCourse(request, teacher);

        // Index course in AI service
        searchService.indexCourse(course);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.toResponse(course));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CourseResponse> updateCourse(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "published", defaultValue = "false") boolean published,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());

        CourseRequest request = new CourseRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategoryId(categoryId);
        request.setLevel(level != null ? DifficultyLevel.valueOf(level) : DifficultyLevel.BEGINNER);
        request.setPublished(published);

        Course course = courseService.updateCourse(id, request, teacher);

        // Re-index in AI service
        searchService.indexCourse(course);

        return ResponseEntity.ok(courseService.toResponse(course));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable Long id,
            Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        courseService.deleteCourse(id, teacher);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseResponse>> getMyTeacherCourses(Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        List<Course> courses = courseService.getTeacherCourses(teacher.getId());
        return ResponseEntity.ok(courseService.toResponseList(courses));
    }
}
