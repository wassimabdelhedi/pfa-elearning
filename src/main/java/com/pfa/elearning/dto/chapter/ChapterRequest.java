package com.pfa.elearning.dto.chapter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for chapter create/update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    private Integer orderIndex;  // Optional, auto-assigned if null
}
