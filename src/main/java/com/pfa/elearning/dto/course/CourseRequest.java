package com.pfa.elearning.dto.course;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for course create/update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;
    private String category;
    private String difficulty;
    private String thumbnailUrl;
    private Boolean published;
}
