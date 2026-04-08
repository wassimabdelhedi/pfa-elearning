package com.pfa.elearning.dto.request;

import com.pfa.elearning.model.DifficultyLevel;
import lombok.Data;

@Data
public class CourseRequest {
    private String title;

    private String description;

    private Long categoryId;

    private DifficultyLevel level;

    private boolean published;
}
