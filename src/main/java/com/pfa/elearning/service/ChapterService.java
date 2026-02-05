package com.pfa.elearning.service;

import com.pfa.elearning.dto.chapter.ChapterRequest;
import com.pfa.elearning.dto.chapter.ChapterResponse;
import com.pfa.elearning.entity.Chapter;
import com.pfa.elearning.entity.Course;
import com.pfa.elearning.exception.ForbiddenException;
import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.mapper.ChapterMapper;
import com.pfa.elearning.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for chapter management operations.
 * Handles CRUD for chapters within courses.
 */
@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final CourseService courseService;
    private final ChapterMapper chapterMapper;

    /**
     * Create a new chapter in a course.
     */
    @Transactional
    public ChapterResponse createChapter(Long courseId, Long teacherId, ChapterRequest request) {
        Course course = courseService.getCourseEntity(courseId);
        validateTeacherOwnership(course, teacherId);

        Integer orderIndex = request.getOrderIndex();
        if (orderIndex == null) {
            orderIndex = chapterRepository.findMaxOrderIndexByCourseId(courseId).orElse(0) + 1;
        }

        Chapter chapter = Chapter.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(orderIndex)
                .course(course)
                .build();

        return chapterMapper.toResponse(chapterRepository.save(chapter));
    }

    /**
     * Get chapter by ID.
     */
    public ChapterResponse getChapterById(Long id) {
        Chapter chapter = getChapterEntity(id);
        return chapterMapper.toDetailedResponse(chapter);
    }

    /**
     * Get chapter entity by ID.
     */
    public Chapter getChapterEntity(Long id) {
        return chapterRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found with id: " + id));
    }

    /**
     * Get all chapters for a course.
     */
    public List<ChapterResponse> getChaptersByCourse(Long courseId) {
        return chapterRepository.findByCourseIdOrderByOrderIndexAsc(courseId).stream()
                .map(chapterMapper::toDetailedResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update chapter.
     */
    @Transactional
    public ChapterResponse updateChapter(Long id, Long teacherId, ChapterRequest request) {
        Chapter chapter = getChapterEntity(id);
        validateTeacherOwnership(chapter.getCourse(), teacherId);

        if (request.getTitle() != null) chapter.setTitle(request.getTitle());
        if (request.getDescription() != null) chapter.setDescription(request.getDescription());
        if (request.getOrderIndex() != null) chapter.setOrderIndex(request.getOrderIndex());

        return chapterMapper.toResponse(chapterRepository.save(chapter));
    }

    /**
     * Reorder chapter within course.
     */
    @Transactional
    public ChapterResponse reorderChapter(Long id, Long teacherId, Integer newOrderIndex) {
        Chapter chapter = getChapterEntity(id);
        validateTeacherOwnership(chapter.getCourse(), teacherId);

        chapter.setOrderIndex(newOrderIndex);
        return chapterMapper.toResponse(chapterRepository.save(chapter));
    }

    /**
     * Delete chapter.
     */
    @Transactional
    public void deleteChapter(Long id, Long teacherId) {
        Chapter chapter = getChapterEntity(id);
        validateTeacherOwnership(chapter.getCourse(), teacherId);

        chapterRepository.delete(chapter);
    }

    /**
     * Validate teacher ownership of course.
     */
    private void validateTeacherOwnership(Course course, Long teacherId) {
        if (!course.getTeacher().getId().equals(teacherId)) {
            throw new ForbiddenException("You can only manage chapters for your own courses");
        }
    }
}
