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
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.nio.file.Path;
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
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());

        // Build course request
        CourseRequest request = new CourseRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategoryId(categoryId);
        request.setLevel(level != null ? DifficultyLevel.valueOf(level) : DifficultyLevel.BEGINNER);
        request.setPublished(published);

        // Handle file upload and text extraction
        if (file != null && !file.isEmpty()) {
            String storedFileName = fileStorageService.storeFile(file);
            request.setFilePath(storedFileName);
            request.setOriginalFileName(file.getOriginalFilename());

            // Extract text from file via AI service
            String extractedText = courseService.extractTextFromFile(file);
            if (extractedText != null && !extractedText.isEmpty()) {
                request.setContent(extractedText);
                log.info("Extracted {} chars from uploaded file: {}",
                        extractedText.length(), file.getOriginalFilename());
            }
        }

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
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());

        // Build course request
        CourseRequest request = new CourseRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setCategoryId(categoryId);
        request.setLevel(level != null ? DifficultyLevel.valueOf(level) : DifficultyLevel.BEGINNER);
        request.setPublished(published);

        // Handle file upload and text extraction
        if (file != null && !file.isEmpty()) {
            // Delete old file if exists
            Course existingCourse = courseService.getCourseById(id);
            if (existingCourse.getFilePath() != null) {
                fileStorageService.deleteFile(existingCourse.getFilePath());
            }

            String storedFileName = fileStorageService.storeFile(file);
            request.setFilePath(storedFileName);
            request.setOriginalFileName(file.getOriginalFilename());

            // Extract text from new file
            String extractedText = courseService.extractTextFromFile(file);
            if (extractedText != null && !extractedText.isEmpty()) {
                request.setContent(extractedText);
                log.info("Re-extracted {} chars from updated file: {}",
                        extractedText.length(), file.getOriginalFilename());
            }
        } else {
            // Keep existing content and file path
            Course existingCourse = courseService.getCourseById(id);
            request.setContent(existingCourse.getContent());
            request.setFilePath(existingCourse.getFilePath());
            request.setOriginalFileName(existingCourse.getOriginalFileName());
        }

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

        // Delete associated file
        Course course = courseService.getCourseById(id);
        if (course.getFilePath() != null) {
            fileStorageService.deleteFile(course.getFilePath());
        }

        courseService.deleteCourse(id, teacher);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-courses")
    public ResponseEntity<List<CourseResponse>> getMyTeacherCourses(Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        List<Course> courses = courseService.getTeacherCourses(teacher.getId());
        return ResponseEntity.ok(courseService.toResponseList(courses));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadCourseFile(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);

        if (course.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = fileStorageService.getFilePath(course.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = course.getOriginalFileName() != null
                        ? course.getOriginalFileName()
                        : course.getFilePath();

                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                        .header("Access-Control-Expose-Headers", "Content-Disposition")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.error("Error downloading file for course {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}/stream")
    public ResponseEntity<Resource> streamCourseVideo(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);

        if (course.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = fileStorageService.getFilePath(course.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = course.getFilePath().toLowerCase();
                String contentType = "application/octet-stream";
                if (fileName.endsWith(".mp4")) contentType = "video/mp4";
                else if (fileName.endsWith(".webm")) contentType = "video/webm";
                else if (fileName.endsWith(".avi")) contentType = "video/x-msvideo";
                else if (fileName.endsWith(".mov")) contentType = "video/quicktime";
                else if (fileName.endsWith(".mkv")) contentType = "video/x-matroska";

                return ResponseEntity.ok()
                        .header("Content-Type", contentType)
                        .header("Accept-Ranges", "bytes")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error streaming video for course {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
