package com.pfa.elearning.mapper;

import com.pfa.elearning.dto.progress.ProgressResponse;
import com.pfa.elearning.entity.Progress;
import org.springframework.stereotype.Component;

/**
 * Mapper for Progress entity to DTO conversions.
 */
@Component
public class ProgressMapper {

    public ProgressResponse toResponse(Progress progress) {
        if (progress == null) return null;

        return ProgressResponse.builder()
                .id(progress.getId())
                .enrollmentId(progress.getEnrollment() != null ? progress.getEnrollment().getId() : null)
                .chapterId(progress.getChapter() != null ? progress.getChapter().getId() : null)
                .chapterTitle(progress.getChapter() != null ? progress.getChapter().getTitle() : null)
                .status(progress.getStatus())
                .completionPercentage(progress.getCompletionPercentage())
                .startedAt(progress.getStartedAt())
                .completedAt(progress.getCompletedAt())
                .lastAccessedAt(progress.getLastAccessedAt())
                .build();
    }
}
