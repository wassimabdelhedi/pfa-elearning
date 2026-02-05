package com.pfa.elearning.mapper;

import com.pfa.elearning.dto.content.ContentResponse;
import com.pfa.elearning.entity.Content;
import org.springframework.stereotype.Component;

/**
 * Mapper for Content entity to DTO conversions.
 */
@Component
public class ContentMapper {

    public ContentResponse toResponse(Content content) {
        if (content == null) return null;

        return ContentResponse.builder()
                .id(content.getId())
                .title(content.getTitle())
                .description(content.getDescription())
                .contentType(content.getContentType())
                .contentUrl(content.getContentUrl())
                .textContent(content.getTextContent())
                .durationMinutes(content.getDurationMinutes())
                .orderIndex(content.getOrderIndex())
                .chapterId(content.getChapter() != null ? content.getChapter().getId() : null)
                .createdAt(content.getCreatedAt())
                .build();
    }
}
