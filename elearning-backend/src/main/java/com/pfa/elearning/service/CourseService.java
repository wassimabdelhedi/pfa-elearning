package com.pfa.elearning.service;

import com.pfa.elearning.dto.request.CourseRequest;
import com.pfa.elearning.dto.response.CourseResponse;
import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.exception.UnauthorizedException;
import com.pfa.elearning.model.Category;
import com.pfa.elearning.model.Course;
import com.pfa.elearning.model.User;
import com.pfa.elearning.repository.CategoryRepository;
import com.pfa.elearning.repository.CourseRatingRepository;
import com.pfa.elearning.repository.CourseRepository;
import com.pfa.elearning.repository.EnrollmentRepository;
import com.pfa.elearning.repository.UserRepository;
import com.pfa.elearning.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRatingRepository courseRatingRepository;
    private final WebClient.Builder webClientBuilder;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.ai-service.base-url}")
    private String aiServiceBaseUrl;

    @Transactional
    public Course createCourse(CourseRequest request, User teacher) {
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .level(request.getLevel())
                .published(request.isPublished())
                .teacher(teacher)
                .build();

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            course.setCategory(category);
        }

        course = courseRepository.save(course);

        if (course.isPublished()) {
            notifyInterestedStudentsAsync(course);
        }

        return course;
    }

    @Transactional
    public Course updateCourse(Long courseId, CourseRequest request, User teacher) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only update your own courses");
        }

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setLevel(request.getLevel());
        course.setPublished(request.isPublished());

        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            course.setCategory(category);
        }

        return courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long courseId, User teacher) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only delete your own courses");
        }

        courseRepository.delete(course);
    }

    @Transactional
    public Course togglePublishStatus(Long courseId, User teacher) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (!course.getTeacher().getId().equals(teacher.getId())) {
            throw new UnauthorizedException("You can only modify your own courses");
        }

        course.setPublished(!course.isPublished());
        course = courseRepository.save(course);

        if (course.isPublished()) {
            notifyInterestedStudentsAsync(course);
        }

        return course;
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
    }

    private void notifyInterestedStudentsAsync(Course course) {
        if (course.getCategory() == null) return;
        String domain = course.getCategory().getName();
        
        List<User> students = userRepository.findByRole(com.pfa.elearning.model.Role.STUDENT);
        for (User student : students) {
            if (domain.equalsIgnoreCase(student.getDomaineInteret())) {
                emailService.sendCourseMatchedInterestEmail(student, course);
            }
        }
    }

    public List<Course> getPublishedCourses() {
        return courseRepository.findByPublishedTrue();
    }

    public List<Course> getTeacherCourses(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }

    public List<Course> getCoursesByCategory(Long categoryId) {
        return courseRepository.findByPublishedTrueAndCategoryId(categoryId);
    }

    public List<Course> searchCourses(String keyword) {
        return courseRepository.searchByKeyword(keyword);
    }

    public List<Course> getTopEnrolledCourses() {
        return courseRepository.findTopEnrolledCourses();
    }

    public List<Course> getPersonalizedCourses(User student) {
        String level = student.getNiveau();
        String domain = student.getDomaineInteret() != null ? student.getDomaineInteret() : "";
        String goal = student.getObjectif() != null ? student.getObjectif() : "";

        // Map French level to DifficultyLevel enum string representation if needed:
        // Débutant -> BEGINNER, Intermédiaire -> INTERMEDIATE, Avancé -> ADVANCED
        String difficultyLevel = "BEGINNER";
        if ("Intermédiaire".equalsIgnoreCase(level)) {
            difficultyLevel = "INTERMEDIATE";
        } else if ("Avancé".equalsIgnoreCase(level)) {
            difficultyLevel = "ADVANCED";
        }

        return courseRepository.findPersonalizedCourses(difficultyLevel, domain, goal);
    }

    /**
     * Calls the AI service to extract text from an uploaded file.
     * Returns the extracted text, or null if extraction fails.
     */
    public String extractTextFromFile(MultipartFile file) {
        try {
            log.info("Sending file to AI service for text extraction: {}", file.getOriginalFilename());

            MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
            bodyBuilder.part("file",
                    new ByteArrayResource(file.getBytes()) {
                        @Override
                        public String getFilename() {
                            return file.getOriginalFilename();
                        }
                    })
                    .contentType(MediaType.APPLICATION_OCTET_STREAM);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = webClientBuilder.build()
                    .post()
                    .uri(aiServiceBaseUrl + "/api/extract-text")
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(bodyBuilder.build()))
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response != null && response.containsKey("text")) {
                String text = (String) response.get("text");
                log.info("AI extracted {} chars from {}", text.length(), file.getOriginalFilename());
                return text;
            }

            return null;
        } catch (Exception e) {
            log.warn("AI text extraction failed for {}: {}", file.getOriginalFilename(), e.getMessage());
            return null;
        }
    }

    public CourseResponse toResponse(Course course) {
        Double avgRating = courseRatingRepository.getAverageRatingByCourseId(course.getId());
        long enrollCount = enrollmentRepository.countByCourseId(course.getId());
        int chapterCount = course.getChapters() != null ? course.getChapters().size() : 0;

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .content(course.getContent())
                .categoryName(course.getCategory() != null ? course.getCategory().getName() : null)
                .categoryId(course.getCategory() != null ? course.getCategory().getId() : null)
                .level(course.getLevel())
                .teacherName(course.getTeacher().getFullName())
                .teacherId(course.getTeacher().getId())
                .keywords(course.getKeywords())
                .published(course.isPublished())
                .averageRating(avgRating != null ? avgRating : 0.0)
                .enrollmentCount(enrollCount)
                .chapterCount(chapterCount)
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    public List<CourseResponse> toResponseList(List<Course> courses) {
        return courses.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
