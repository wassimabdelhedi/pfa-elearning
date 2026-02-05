package com.pfa.elearning.repository;

import com.pfa.elearning.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Chapter entity operations.
 */
@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Long> {

    List<Chapter> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    Optional<Chapter> findByCourseIdAndOrderIndex(Long courseId, Integer orderIndex);

    @Query("SELECT MAX(c.orderIndex) FROM Chapter c WHERE c.course.id = :courseId")
    Optional<Integer> findMaxOrderIndexByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT COUNT(c) FROM Chapter c WHERE c.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
}
