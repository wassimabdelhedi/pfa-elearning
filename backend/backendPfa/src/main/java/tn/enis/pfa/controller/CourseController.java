package tn.enis.pfa.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.enis.pfa.dto.*;
import tn.enis.pfa.security.CurrentUserId;
import tn.enis.pfa.service.CourseService;

import java.util.List;

@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<List<CourseDto>> findAll(@RequestParam(required = false) String category) {
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(courseService.findByCategory(category));
        }
        return ResponseEntity.ok(courseService.findAll());
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<CourseDto>> findByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.findByTeacher(teacherId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CourseDto> create(@CurrentUserId Long teacherId,
                                            @Valid @RequestBody CourseCreateRequest request) {
        return ResponseEntity.ok(courseService.create(teacherId, request));
    }

    @PostMapping("/{courseId}/modules")
    public ResponseEntity<ModuleDto> addModule(@PathVariable Long courseId,
                                               @Valid @RequestBody ModuleCreateRequest request) {
        return ResponseEntity.ok(courseService.addModule(courseId, request));
    }

    @PostMapping("/modules/{moduleId}/contents")
    public ResponseEntity<ContentDto> addContent(@PathVariable Long moduleId,
                                                 @Valid @RequestBody ContentCreateRequest request) {
        return ResponseEntity.ok(courseService.addContent(moduleId, request));
    }

    @PostMapping("/modules/{moduleId}/exercises")
    public ResponseEntity<ExerciseDto> addExercise(@PathVariable Long moduleId,
                                                   @Valid @RequestBody ExerciseCreateRequest request) {
        return ResponseEntity.ok(courseService.addExercise(moduleId, request));
    }
}
