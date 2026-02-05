package com.pfa.elearning.mapper;

import com.pfa.elearning.dto.chapter.ChapterResponse;
import com.pfa.elearning.entity.Chapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper for Chapter entity to DTO conversions.
 */
@Component
@RequiredArgsConstructor
public class ChapterMapper {

    private final ContentMapper contentMapper;

    public ChapterResponse toResponse(Chapter chapter) {
        if (chapter == null) return null;

        return ChapterResponse.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .description(chapter.getDescription())
                .orderIndex(chapter.getOrderIndex())
                .courseId(chapter.getCourse() != null ? chapter.getCourse().getId() : null)
                .createdAt(chapter.getCreatedAt())
                .totalContents(chapter.getContents() != null ? chapter.getContents().size() : 0)
                .totalDurationMinutes(calculateTotalDuration(chapter))
                .build();
    }

    public ChapterResponse toDetailedResponse(Chapter chapter) {
        if (chapter == null) return null;

        ChapterResponse response = toResponse(chapter);
        if (chapter.getContents() != null) {
            response.setContents(chapter.getContents().stream()
                    .map(contentMapper::toResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    private int calculateTotalDuration(Chapter chapter) {
        if (chapter.getContents() == null) return 0;
        return chapter.getContents().stream()
                .mapToInt(content -> content.getDurationMinutes() != null ? content.getDurationMinutes() : 0)
                .sum();
    }
}
