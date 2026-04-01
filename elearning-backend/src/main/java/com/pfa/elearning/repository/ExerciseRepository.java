package com.pfa.elearning.repository;

import com.pfa.elearning.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    List<Exercise> findByPublishedTrue();
    List<Exercise> findByTeacherId(Long teacherId);
    List<Exercise> findByCourseId(Long courseId);

    @Query("SELECT e FROM Exercise e WHERE e.published = true AND " +
           "(LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Exercise> searchByKeyword(@Param("keyword") String keyword);
}
