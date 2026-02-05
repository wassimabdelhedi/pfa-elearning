package com.pfa.elearning.dto.progress;

import com.pfa.elearning.enums.ProgressStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for progress update requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequest {

    @NotNull(message = "Chapter ID is required")
    private Long chapterId;

    private ProgressStatus status;

    @Min(value = 0, message = "Completion percentage must be at least 0")
    @Max(value = 100, message = "Completion percentage must be at most 100")
    private Double completionPercentage;
}
