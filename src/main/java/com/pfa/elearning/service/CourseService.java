package com.pfa.elearning.service;

import com.pfa.elearning.dto.course.CourseRequest;
import com.pfa.elearning.dto.course.CourseResponse;
import com.pfa.elearning.entity.Course;
import com.pfa.elearning.entity.User;
import com.pfa.elearning.enums.Role;
import com.pfa.elearning.exception.ForbiddenException;
import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.mapper.CourseMapper;
import com.pfa.elearning.repository.CourseRepository;
import com.pfa.elearning.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for course management operations.
 * Handles CRUD for courses (teacher access).
 */
@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final CourseMapper courseMapper;

    /**
     * Create a new course (teacher only).
     */
    @Transactional
    public CourseResponse createCourse(Long teacherId, CourseRequest request) {
        User teacher = userService.getUserEntityById(teacherId);
        
        if (teacher.getRole() != Role.TEACHER && teacher.getRole() != Role.ADMIN) {
            throw new ForbiddenException("Only teachers can create courses");
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .difficulty(request.getDifficulty())
                .thumbnailUrl(request.getThumbnailUrl())
                .published(request.getPublished() != null ? request.getPublished() : false)
                .teacher(teacher)
                .build();

        return courseMapper.toResponse(courseRepository.save(course));
    }

    /**
     * Get course by ID.
     */
    public CourseResponse getCourseById(Long id) {
        Course course = getCourseEntity(id);
        CourseResponse response = courseMapper.toDetailedResponse(course);
        response.setEnrollmentCount(enrollmentRepository.countByCourseId(id));
        return response;
    }

    /**
     * Get course entity by ID.
     */
    public Course getCourseEntity(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + id));
    }

    /**
     * Get all published courses.
     */
    public List<CourseResponse> getAllPublishedCourses() {
        return courseRepository.findByPublishedTrue().stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get courses by teacher.
     */
    public List<CourseResponse> getCoursesByTeacher(Long teacherId) {
        return courseRepository.findByTeacherId(teacherId).stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search courses by keyword.
     */
    public List<CourseResponse> searchCourses(String keyword) {
        return courseRepository.searchByKeyword(keyword).stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get courses by category.
     */
    public List<CourseResponse> getCoursesByCategory(String category) {
        return courseRepository.findPublishedByCategory(category).stream()
                .map(courseMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all categories.
     */
    public List<String> getAllCategories() {
        return courseRepository.findAllCategories();
    }

    /**
     * Update course (owner teacher only).
     */
    @Transactional
    public CourseResponse updateCourse(Long id, Long teacherId, CourseRequest request) {
        Course course = getCourseEntity(id);
        
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ForbiddenException("You can only update your own courses");
        }

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getCategory() != null) course.setCategory(request.getCategory());
        if (request.getDifficulty() != null) course.setDifficulty(request.getDifficulty());
        if (request.getThumbnailUrl() != null) course.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getPublished() != null) course.setPublished(request.getPublished());

        return courseMapper.toResponse(courseRepository.save(course));
    }

    /**
     * Publish/unpublish course.
     */
    @Transactional
    public CourseResponse togglePublishStatus(Long id, Long teacherId) {
        Course course = getCourseEntity(id);
        
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ForbiddenException("You can only publish your own courses");
        }

        course.setPublished(!course.getPublished());
        return courseMapper.toResponse(courseRepository.save(course));
    }

    /**
     * Delete course (owner teacher only).
     */
    @Transactional
    public void deleteCourse(Long id, Long teacherId) {
        Course course = getCourseEntity(id);
        
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ForbiddenException("You can only delete your own courses");
        }

        courseRepository.delete(course);
    }
}
