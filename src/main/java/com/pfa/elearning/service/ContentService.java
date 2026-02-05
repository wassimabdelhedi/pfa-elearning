package com.pfa.elearning.service;

import com.pfa.elearning.dto.content.ContentRequest;
import com.pfa.elearning.dto.content.ContentResponse;
import com.pfa.elearning.entity.Chapter;
import com.pfa.elearning.entity.Content;
import com.pfa.elearning.exception.ForbiddenException;
import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.mapper.ContentMapper;
import com.pfa.elearning.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for content management operations.
 * Handles CRUD for content within chapters.
 */
@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepository;
    private final ChapterService chapterService;
    private final ContentMapper contentMapper;

    /**
     * Create new content in a chapter.
     */
    @Transactional
    public ContentResponse createContent(Long chapterId, Long teacherId, ContentRequest request) {
        Chapter chapter = chapterService.getChapterEntity(chapterId);
        validateTeacherOwnership(chapter, teacherId);

        Integer orderIndex = request.getOrderIndex();
        if (orderIndex == null) {
            orderIndex = contentRepository.findMaxOrderIndexByChapterId(chapterId).orElse(0) + 1;
        }

        Content content = Content.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .contentType(request.getContentType())
                .contentUrl(request.getContentUrl())
                .textContent(request.getTextContent())
                .durationMinutes(request.getDurationMinutes())
                .orderIndex(orderIndex)
                .chapter(chapter)
                .build();

        return contentMapper.toResponse(contentRepository.save(content));
    }

    /**
     * Get content by ID.
     */
    public ContentResponse getContentById(Long id) {
        Content content = getContentEntity(id);
        return contentMapper.toResponse(content);
    }

    /**
     * Get content entity by ID.
     */
    public Content getContentEntity(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + id));
    }

    /**
     * Get all contents for a chapter.
     */
    public List<ContentResponse> getContentsByChapter(Long chapterId) {
        return contentRepository.findByChapterIdOrderByOrderIndexAsc(chapterId).stream()
                .map(contentMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update content.
     */
    @Transactional
    public ContentResponse updateContent(Long id, Long teacherId, ContentRequest request) {
        Content content = getContentEntity(id);
        validateTeacherOwnership(content.getChapter(), teacherId);

        if (request.getTitle() != null) content.setTitle(request.getTitle());
        if (request.getDescription() != null) content.setDescription(request.getDescription());
        if (request.getContentType() != null) content.setContentType(request.getContentType());
        if (request.getContentUrl() != null) content.setContentUrl(request.getContentUrl());
        if (request.getTextContent() != null) content.setTextContent(request.getTextContent());
        if (request.getDurationMinutes() != null) content.setDurationMinutes(request.getDurationMinutes());
        if (request.getOrderIndex() != null) content.setOrderIndex(request.getOrderIndex());

        return contentMapper.toResponse(contentRepository.save(content));
    }

    /**
     * Delete content.
     */
    @Transactional
    public void deleteContent(Long id, Long teacherId) {
        Content content = getContentEntity(id);
        validateTeacherOwnership(content.getChapter(), teacherId);

        contentRepository.delete(content);
    }

    /**
     * Validate teacher ownership of chapter's course.
     */
    private void validateTeacherOwnership(Chapter chapter, Long teacherId) {
        if (!chapter.getCourse().getTeacher().getId().equals(teacherId)) {
            throw new ForbiddenException("You can only manage content for your own courses");
        }
    }
}
