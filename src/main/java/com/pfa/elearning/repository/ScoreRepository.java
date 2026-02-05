package com.pfa.elearning.repository;

import com.pfa.elearning.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Score entity operations.
 * Prepared for future recommendation engine integration.
 */
@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByUserId(Long userId);

    List<Score> findByUserIdAndCourseId(Long userId, Long courseId);

    List<Score> findByUserIdOrderByAttemptedAtDesc(Long userId);

    @Query("SELECT AVG(s.scoreValue) FROM Score s WHERE s.user.id = :userId")
    Optional<Double> getAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT AVG(s.scoreValue) FROM Score s WHERE s.user.id = :userId AND s.course.id = :courseId")
    Optional<Double> getAverageScoreByUserIdAndCourseId(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);

    @Query("SELECT MAX(s.scoreValue) FROM Score s WHERE s.user.id = :userId " +
           "AND s.assessmentId = :assessmentId")
    Optional<Double> getBestScoreByAssessment(
            @Param("userId") Long userId,
            @Param("assessmentId") String assessmentId);
}
