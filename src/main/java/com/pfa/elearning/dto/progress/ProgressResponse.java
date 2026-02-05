package com.pfa.elearning.dto.progress;

import com.pfa.elearning.enums.ProgressStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for progress response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {

    private Long id;
    private Long enrollmentId;
    private Long chapterId;
    private String chapterTitle;
    private ProgressStatus status;
    private Double completionPercentage;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}
