package com.pfa.elearning.service;

import com.pfa.elearning.dto.progress.ProgressResponse;
import com.pfa.elearning.dto.progress.ProgressUpdateRequest;
import com.pfa.elearning.entity.Chapter;
import com.pfa.elearning.entity.Enrollment;
import com.pfa.elearning.entity.Progress;
import com.pfa.elearning.enums.ProgressStatus;
import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.mapper.ProgressMapper;
import com.pfa.elearning.repository.ChapterRepository;
import com.pfa.elearning.repository.ProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for progress tracking operations.
 * Handles tracking and updating chapter-level progress.
 */
@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final ChapterRepository chapterRepository;
    private final EnrollmentService enrollmentService;
    private final ProgressMapper progressMapper;

    /**
     * Update progress for a chapter.
     */
    @Transactional
    public ProgressResponse updateProgress(Long userId, Long courseId, ProgressUpdateRequest request) {
        Enrollment enrollment = enrollmentService.getEnrollmentEntity(userId, courseId);
        Chapter chapter = chapterRepository.findById(request.getChapterId())
                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found"));

        // Get or create progress record
        Progress progress = progressRepository.findByEnrollmentIdAndChapterId(
                        enrollment.getId(), request.getChapterId())
                .orElseGet(() -> Progress.builder()
                        .enrollment(enrollment)
                        .chapter(chapter)
                        .status(ProgressStatus.NOT_STARTED)
                        .completionPercentage(0.0)
                        .build());

        // Update progress
        if (request.getStatus() != null) {
            progress.setStatus(request.getStatus());
            if (request.getStatus() == ProgressStatus.IN_PROGRESS && progress.getStartedAt() == null) {
                progress.setStartedAt(LocalDateTime.now());
            }
            if (request.getStatus() == ProgressStatus.COMPLETED) {
                progress.setCompletedAt(LocalDateTime.now());
                progress.setCompletionPercentage(100.0);
            }
        }

        if (request.getCompletionPercentage() != null) {
            progress.setCompletionPercentage(request.getCompletionPercentage());
            if (request.getCompletionPercentage() >= 100.0) {
                progress.setStatus(ProgressStatus.COMPLETED);
                progress.setCompletedAt(LocalDateTime.now());
            } else if (request.getCompletionPercentage() > 0) {
                progress.setStatus(ProgressStatus.IN_PROGRESS);
                if (progress.getStartedAt() == null) {
                    progress.setStartedAt(LocalDateTime.now());
                }
            }
        }

        progress.setLastAccessedAt(LocalDateTime.now());
        Progress savedProgress = progressRepository.save(progress);

        // Update overall enrollment progress
        updateEnrollmentProgress(enrollment);

        return progressMapper.toResponse(savedProgress);
    }

    /**
     * Get progress for all chapters in a course.
     */
    public List<ProgressResponse> getCourseProgress(Long userId, Long courseId) {
        Enrollment enrollment = enrollmentService.getEnrollmentEntity(userId, courseId);
        return progressRepository.findByEnrollmentId(enrollment.getId()).stream()
                .map(progressMapper::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get progress for a specific chapter.
     */
    public ProgressResponse getChapterProgress(Long userId, Long courseId, Long chapterId) {
        Enrollment enrollment = enrollmentService.getEnrollmentEntity(userId, courseId);
        Progress progress = progressRepository.findByEnrollmentIdAndChapterId(enrollment.getId(), chapterId)
                .orElse(Progress.builder()
                        .enrollment(enrollment)
                        .chapter(chapterRepository.findById(chapterId)
                                .orElseThrow(() -> new ResourceNotFoundException("Chapter not found")))
                        .status(ProgressStatus.NOT_STARTED)
                        .completionPercentage(0.0)
                        .build());
        return progressMapper.toResponse(progress);
    }

    /**
     * Mark chapter as completed.
     */
    @Transactional
    public ProgressResponse markChapterCompleted(Long userId, Long courseId, Long chapterId) {
        ProgressUpdateRequest request = ProgressUpdateRequest.builder()
                .chapterId(chapterId)
                .status(ProgressStatus.COMPLETED)
                .completionPercentage(100.0)
                .build();
        return updateProgress(userId, courseId, request);
    }

    /**
     * Update overall enrollment progress based on chapter progress.
     */
    private void updateEnrollmentProgress(Enrollment enrollment) {
        Long totalChapters = chapterRepository.countByCourseId(enrollment.getCourse().getId());
        if (totalChapters == 0) return;

        Long completedChapters = progressRepository.countCompletedByEnrollmentId(enrollment.getId());
        Double completionPercentage = (completedChapters.doubleValue() / totalChapters.doubleValue()) * 100;

        enrollmentService.updateEnrollmentProgress(
                enrollment.getUser().getId(),
                enrollment.getCourse().getId(),
                completionPercentage
        );
    }
}
