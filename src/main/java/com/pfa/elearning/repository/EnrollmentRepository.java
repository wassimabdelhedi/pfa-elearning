package com.pfa.elearning.repository;

import com.pfa.elearning.entity.Enrollment;
import com.pfa.elearning.enums.ProgressStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Enrollment entity operations.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByUserIdAndCourseId(Long userId, Long courseId);

    List<Enrollment> findByUserId(Long userId);

    List<Enrollment> findByCourseId(Long courseId);

    List<Enrollment> findByUserIdAndStatus(Long userId, ProgressStatus status);

    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId ORDER BY e.lastAccessedAt DESC")
    List<Enrollment> findByUserIdOrderByLastAccessedAtDesc(@Param("userId") Long userId);

    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.status = 'COMPLETED'")
    List<Enrollment> findCompletedByUserId(@Param("userId") Long userId);

    @Query("SELECT e FROM Enrollment e WHERE e.user.id = :userId AND e.status = 'IN_PROGRESS'")
    List<Enrollment> findInProgressByUserId(@Param("userId") Long userId);
}
