package com.pfa.elearning.repository;

import com.pfa.elearning.entity.LearningHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for LearningHistory entity operations.
 * Prepared for future recommendation engine integration.
 */
@Repository
public interface LearningHistoryRepository extends JpaRepository<LearningHistory, Long> {

    List<LearningHistory> findByUserId(Long userId);

    List<LearningHistory> findByUserIdAndCourseId(Long userId, Long courseId);

    List<LearningHistory> findByUserIdOrderByActivityTimestampDesc(Long userId);

    @Query("SELECT lh FROM LearningHistory lh WHERE lh.user.id = :userId " +
           "AND lh.activityTimestamp BETWEEN :startDate AND :endDate")
    List<LearningHistory> findByUserIdAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT lh.activityType, COUNT(lh) FROM LearningHistory lh " +
           "WHERE lh.user.id = :userId GROUP BY lh.activityType")
    List<Object[]> countActivitiesByType(@Param("userId") Long userId);
}
