package com.pfa.elearning.repository;

import com.pfa.elearning.entity.Progress;
import com.pfa.elearning.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Progress entity operations.
 */
@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {

    Optional<Progress> findByEnrollmentIdAndChapterId(Long enrollmentId, Long chapterId);

    List<Progress> findByEnrollmentId(Long enrollmentId);

    List<Progress> findByEnrollmentIdAndStatus(Long enrollmentId, ProgressStatus status);

    @Query("SELECT COUNT(p) FROM Progress p WHERE p.enrollment.id = :enrollmentId AND p.status = 'COMPLETED'")
    Long countCompletedByEnrollmentId(@Param("enrollmentId") Long enrollmentId);

    @Query("SELECT AVG(p.completionPercentage) FROM Progress p WHERE p.enrollment.id = :enrollmentId")
    Optional<Double> getAverageCompletionByEnrollmentId(@Param("enrollmentId") Long enrollmentId);
}
