package com.pfa.elearning.repository;

import com.pfa.elearning.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByPublishedTrue();
    List<Quiz> findByTeacherId(Long teacherId);
    List<Quiz> findByCourseId(Long courseId);

    @Query("SELECT q FROM Quiz q WHERE q.published = true AND " +
           "(LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(q.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Quiz> searchByKeyword(@Param("keyword") String keyword);
}
