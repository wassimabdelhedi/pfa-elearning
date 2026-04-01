package com.pfa.elearning.service;

import com.pfa.elearning.dto.response.RecommendationResponse;
import com.pfa.elearning.model.Recommendation;
import com.pfa.elearning.repository.CourseRatingRepository;
import com.pfa.elearning.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final CourseRatingRepository courseRatingRepository;

    public List<RecommendationResponse> getStudentRecommendations(Long studentId) {
        List<Recommendation> recommendations =
                recommendationRepository.findTop10ByStudentIdOrderByRecommendedAtDesc(studentId);

        return recommendations.stream().map(rec -> {
            Double avgRating = courseRatingRepository.getAverageRatingByCourseId(rec.getCourse().getId());
            return RecommendationResponse.builder()
                    .courseId(rec.getCourse().getId())
                    .courseTitle(rec.getCourse().getTitle())
                    .courseDescription(rec.getCourse().getDescription())
                    .categoryName(rec.getCourse().getCategory() != null
                            ? rec.getCourse().getCategory().getName() : null)
                    .teacherName(rec.getCourse().getTeacher().getFullName())
                    .level(rec.getCourse().getLevel() != null
                            ? rec.getCourse().getLevel().name() : null)
                    .relevanceScore(rec.getRelevanceScore())
                    .reason(rec.getReason())
                    .averageRating(avgRating != null ? avgRating : 0.0)
                    .build();
        }).collect(Collectors.toList());
    }

    public void markAsClicked(Long recommendationId) {
        recommendationRepository.findById(recommendationId).ifPresent(rec -> {
            rec.setClicked(true);
            recommendationRepository.save(rec);
        });
    }
}
