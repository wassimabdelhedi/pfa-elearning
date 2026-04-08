package com.pfa.elearning.dto.response;

import com.pfa.elearning.model.DifficultyLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String content;
    private String categoryName;
    private Long categoryId;
    private DifficultyLevel level;
    private String teacherName;
    private Long teacherId;
    private String keywords;
    private boolean published;
    private double averageRating;
    private long enrollmentCount;
    private int chapterCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
