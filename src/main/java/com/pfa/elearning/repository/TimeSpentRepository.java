package com.pfa.elearning.repository;

import com.pfa.elearning.entity.TimeSpent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TimeSpent entity operations.
 * Prepared for future recommendation engine integration.
 */
@Repository
public interface TimeSpentRepository extends JpaRepository<TimeSpent, Long> {

    List<TimeSpent> findByUserId(Long userId);

    List<TimeSpent> findByUserIdAndContentId(Long userId, Long contentId);

    @Query("SELECT SUM(ts.durationSeconds) FROM TimeSpent ts WHERE ts.user.id = :userId")
    Optional<Long> getTotalTimeSpentByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(ts.durationSeconds) FROM TimeSpent ts WHERE ts.user.id = :userId " +
           "AND ts.content.chapter.course.id = :courseId")
    Optional<Long> getTotalTimeSpentByUserIdAndCourseId(
            @Param("userId") Long userId,
            @Param("courseId") Long courseId);

    @Query("SELECT ts.content.contentType, SUM(ts.durationSeconds) FROM TimeSpent ts " +
           "WHERE ts.user.id = :userId GROUP BY ts.content.contentType")
    List<Object[]> getTimeSpentByContentType(@Param("userId") Long userId);
}
