package com.pfa.elearning.repository;

import com.pfa.elearning.model.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByStudentIdOrderByRelevanceScoreDesc(Long studentId);
    List<Recommendation> findTop10ByStudentIdOrderByRecommendedAtDesc(Long studentId);
    void deleteByStudentId(Long studentId);
    List<Recommendation> findByStudentIdAndCourseId(Long studentId, Long courseId);
}
