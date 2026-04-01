package com.pfa.elearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private Long courseId;
    private String courseTitle;
    private String courseDescription;
    private String categoryName;
    private String teacherName;
    private String level;
    private double relevanceScore;
    private String reason;
    private double averageRating;
}
