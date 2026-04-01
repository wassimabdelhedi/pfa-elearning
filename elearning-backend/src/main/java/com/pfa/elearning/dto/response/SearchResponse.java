package com.pfa.elearning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    private String query;
    private List<String> extractedKeywords;
    private List<RecommendationResponse> recommendations;
    private List<Map<String, Object>> exercises;
    private List<Map<String, Object>> quizzes;
    private int totalResults;
}
