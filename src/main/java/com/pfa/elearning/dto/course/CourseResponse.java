package com.pfa.elearning.dto.course;

import com.pfa.elearning.dto.chapter.ChapterResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for course response with full details including chapters.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String difficulty;
    private String thumbnailUrl;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Teacher info
    private Long teacherId;
    private String teacherName;

    // Statistics
    private Integer totalChapters;
    private Integer totalContents;
    private Long enrollmentCount;

    // Chapters (optional, for detailed view)
    private List<ChapterResponse> chapters;
}
