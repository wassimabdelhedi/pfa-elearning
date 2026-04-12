package com.pfa.elearning.controller;

import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.exception.UnauthorizedException;
import com.pfa.elearning.model.*;
import com.pfa.elearning.repository.CategoryRepository;
import com.pfa.elearning.repository.ExerciseCompletionRepository;
import com.pfa.elearning.repository.ExerciseRepository;
import com.pfa.elearning.repository.EnrollmentRepository;
import com.pfa.elearning.service.CourseService;
import com.pfa.elearning.service.FileStorageService;
import com.pfa.elearning.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Slf4j
public class ExerciseController {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseCompletionRepository exerciseCompletionRepository;
    private final UserService userService;
    private final FileStorageService fileStorageService;
    private final CategoryRepository categoryRepository;
    private final CourseService courseService;
    private final EnrollmentRepository enrollmentRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getPublishedExercises() {
        List<Exercise> exercises = exerciseRepository.findByPublishedTrue();
        return ResponseEntity.ok(exercises.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getExerciseById(@PathVariable Long id, Authentication authentication) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));

        // Security check for students
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            User student = userService.getUserByEmail(authentication.getName());
            if (exercise.getCourse() != null) {
                java.util.Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), exercise.getCourse().getId());
                if (enrollmentOpt.isEmpty() || !enrollmentOpt.get().isCompleted()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } else {
                // Exercises not attached to a course are not accessible to students
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        return ResponseEntity.ok(toMap(exercise));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Map<String, Object>>> getExercisesByCourse(@PathVariable Long courseId, Authentication authentication) {
        if (authentication != null && authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            User student = userService.getUserByEmail(authentication.getName());
            java.util.Optional<Enrollment> enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(student.getId(), courseId);
            if (enrollmentOpt.isEmpty() || !enrollmentOpt.get().isCompleted()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        List<Exercise> exercises = exerciseRepository.findByCourseId(courseId)
                                                     .stream()
                                                     .filter(Exercise::isPublished)
                                                     .collect(Collectors.toList());
        return ResponseEntity.ok(exercises.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @GetMapping("/my-exercises")
    public ResponseEntity<List<Map<String, Object>>> getMyExercises(Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        List<Exercise> exercises = exerciseRepository.findByTeacherId(teacher.getId());
        return ResponseEntity.ok(exercises.stream().map(this::toMap).collect(Collectors.toList()));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> createExercise(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "courseId", required = false) Long courseId,
            @RequestParam(value = "level", required = false) String level,
            @RequestParam(value = "published", defaultValue = "false") boolean published,
            @RequestParam(value = "file", required = false) MultipartFile file,
            Authentication authentication) {

        User teacher = userService.getUserByEmail(authentication.getName());

        Exercise exercise = Exercise.builder()
                .title(title)
                .description(description)
                .level(level != null ? DifficultyLevel.valueOf(level) : DifficultyLevel.BEGINNER)
                .published(published)
                .teacher(teacher)
                .build();

        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
            exercise.setCategory(category);
        }

        if (courseId != null) {
            Course course = courseService.getCourseById(courseId);
            exercise.setCourse(course);
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "Le cours est obligatoire pour créer un exercice"));
        }

        if (file != null && !file.isEmpty()) {
            String storedFileName = fileStorageService.storeFile(file);
            exercise.setFilePath(storedFileName);
            exercise.setOriginalFileName(file.getOriginalFilename());
        }

        exercise = exerciseRepository.save(exercise);
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(exercise));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExercise(@PathVariable Long id, Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));

        if (!exercise.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only delete your own exercises");
        }

        if (exercise.getFilePath() != null) {
            fileStorageService.deleteFile(exercise.getFilePath());
        }

        exerciseRepository.delete(exercise);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/toggle-publish")
    public ResponseEntity<Map<String, Object>> togglePublishExercise(
            @PathVariable Long id,
            Authentication authentication) {
        User teacher = userService.getUserByEmail(authentication.getName());
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));

        if (!exercise.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only modify your own exercises");
        }

        exercise.setPublished(!exercise.isPublished());
        exercise = exerciseRepository.save(exercise);
        return ResponseEntity.ok(toMap(exercise));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadExerciseFile(@PathVariable Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));

        if (exercise.getFilePath() == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path filePath = fileStorageService.getFilePath(exercise.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String fileName = exercise.getOriginalFileName() != null
                        ? exercise.getOriginalFileName() : exercise.getFilePath();
                return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                        .header("Access-Control-Expose-Headers", "Content-Disposition")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error downloading file for exercise {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // ===== STUDENT COMPLETIONS =====
    @PostMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeExercise(
            @PathVariable Long id,
            Authentication authentication) {
        
        User student = userService.getUserByEmail(authentication.getName());
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));

        if (exerciseCompletionRepository.existsByStudentIdAndExerciseId(student.getId(), exercise.getId())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Exercise already completed"));
        }

        ExerciseCompletion completion = ExerciseCompletion.builder()
                .student(student)
                .exercise(exercise)
                .build();

        completion = exerciseCompletionRepository.save(completion);

        return ResponseEntity.ok(Map.of(
            "id", completion.getId(),
            "exerciseId", exercise.getId(),
            "completedAt", completion.getCompletedAt() != null ? completion.getCompletedAt() : ""
        ));
    }

    @GetMapping("/my-completed-exercises")
    public ResponseEntity<List<Map<String, Object>>> getMyCompletedExercises(Authentication authentication) {
        User student = userService.getUserByEmail(authentication.getName());
        List<ExerciseCompletion> completions = exerciseCompletionRepository.findByStudentId(student.getId());
        
        List<Map<String, Object>> result = completions.stream().map(c -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", c.getId());
            map.put("exerciseId", c.getExercise().getId());
            map.put("exerciseTitle", c.getExercise().getTitle());
            map.put("completedAt", c.getCompletedAt());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toMap(Exercise e) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", e.getId());
        map.put("title", e.getTitle());
        map.put("description", e.getDescription());
        map.put("content", e.getContent());
        map.put("level", e.getLevel());
        map.put("categoryName", e.getCategory() != null ? e.getCategory().getName() : null);
        map.put("categoryId", e.getCategory() != null ? e.getCategory().getId() : null);
        map.put("teacherName", e.getTeacher().getFullName());
        map.put("teacherId", e.getTeacher().getId());
        map.put("courseName", e.getCourse() != null ? e.getCourse().getTitle() : null);
        map.put("courseId", e.getCourse() != null ? e.getCourse().getId() : null);
        map.put("filePath", e.getFilePath());
        map.put("originalFileName", e.getOriginalFileName());
        map.put("published", e.isPublished());
        map.put("createdAt", e.getCreatedAt());
        return map;
    }
}
