package tn.enis.pfa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enis.pfa.dto.*;
import tn.enis.pfa.entity.Content;
import tn.enis.pfa.entity.Course;
import tn.enis.pfa.entity.CourseModule;
import tn.enis.pfa.entity.Exercise;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.repository.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final ContentRepository contentRepository;
    private final ExerciseRepository exerciseRepository;

    public List<CourseDto> findAll() {
        return courseRepository.findAll().stream()
                .map(CourseDto::fromSummary)
                .collect(Collectors.toList());
    }

    public List<CourseDto> findByCategory(String category) {
        return courseRepository.findByCategory(category).stream()
                .map(CourseDto::fromSummary)
                .collect(Collectors.toList());
    }

    public List<CourseDto> findByTeacher(Long teacherId) {
        User teacher = userRepository.findById(teacherId).orElseThrow();
        return courseRepository.findByTeacher(teacher).stream()
                .map(CourseDto::fromSummary)
                .collect(Collectors.toList());
    }

    public CourseDto findById(Long id) {
        Course course = courseRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Cours non trouvé"));
        return CourseDto.from(course);
    }

    @Transactional
    public CourseDto create(Long teacherId, CourseCreateRequest request) {
        User teacher = userRepository.findById(teacherId).orElseThrow(() -> new IllegalArgumentException("Enseignant non trouvé"));
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .teacher(teacher)
                .build();
        course = courseRepository.save(course);
        return CourseDto.fromSummary(course);
    }

    @Transactional
    public ModuleDto addModule(Long courseId, ModuleCreateRequest request) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Cours non trouvé"));
        CourseModule module = CourseModule.builder()
                .title(request.getTitle())
                .orderIndex(request.getOrderIndex())
                .course(course)
                .build();
        module = courseModuleRepository.save(module);
        return ModuleDto.from(module);
    }

    @Transactional
    public ContentDto addContent(Long moduleId, ContentCreateRequest request) {
        CourseModule module = courseModuleRepository.findById(moduleId).orElseThrow(() -> new IllegalArgumentException("Module non trouvé"));
        Content.ContentType type = Content.ContentType.valueOf(request.getType().toUpperCase());
        Content content = Content.builder()
                .title(request.getTitle())
                .type(type)
                .body(request.getBody())
                .videoUrl(request.getVideoUrl())
                .orderIndex(request.getOrderIndex())
                .module(module)
                .build();
        content = contentRepository.save(content);
        return ContentDto.from(content);
    }

    @Transactional
    public ExerciseDto addExercise(Long moduleId, ExerciseCreateRequest request) {
        CourseModule module = courseModuleRepository.findById(moduleId).orElseThrow(() -> new IllegalArgumentException("Module non trouvé"));
        Exercise.ExerciseType type = Exercise.ExerciseType.valueOf(request.getType().toUpperCase());
        Exercise exercise = Exercise.builder()
                .question(request.getQuestion())
                .type(type)
                .correctAnswer(request.getCorrectAnswer())
                .optionsJson(request.getOptionsJson())
                .orderIndex(request.getOrderIndex())
                .module(module)
                .build();
        exercise = exerciseRepository.save(exercise);
        return ExerciseDto.from(exercise);
    }
}
