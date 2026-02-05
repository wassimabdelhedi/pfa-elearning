package com.pfa.elearning.dto.content;

import com.pfa.elearning.enums.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for content response.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResponse {

    private Long id;
    private String title;
    private String description;
    private ContentType contentType;
    private String contentUrl;
    private String textContent;
    private Integer durationMinutes;
    private Integer orderIndex;
    private Long chapterId;
    private LocalDateTime createdAt;
}
