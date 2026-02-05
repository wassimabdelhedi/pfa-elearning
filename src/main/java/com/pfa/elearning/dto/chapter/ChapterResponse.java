package com.pfa.elearning.dto.chapter;

import com.pfa.elearning.dto.content.ContentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for chapter response with contents.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterResponse {

    private Long id;
    private String title;
    private String description;
    private Integer orderIndex;
    private Long courseId;
    private LocalDateTime createdAt;

    // Statistics
    private Integer totalContents;
    private Integer totalDurationMinutes;

    // Contents (optional, for detailed view)
    private List<ContentResponse> contents;
}
