package com.pfa.elearning.dto.content;

import com.pfa.elearning.enums.ContentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for content create/update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Content type is required")
    private ContentType contentType;

    private String contentUrl;      // For VIDEO/PDF
    private String textContent;     // For TEXT type
    private Integer durationMinutes;
    private Integer orderIndex;     // Optional, auto-assigned if null
}
