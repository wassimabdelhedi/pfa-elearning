package com.pfa.elearning.repository;

import com.pfa.elearning.entity.Course;
import com.pfa.elearning.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Course entity operations.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacher(User teacher);

    List<Course> findByTeacherId(Long teacherId);

    List<Course> findByPublishedTrue();

    List<Course> findByCategory(String category);

    List<Course> findByDifficulty(String difficulty);

    @Query("SELECT c FROM Course c WHERE c.published = true AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Course> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c FROM Course c WHERE c.published = true AND c.category = :category")
    List<Course> findPublishedByCategory(@Param("category") String category);

    @Query("SELECT DISTINCT c.category FROM Course c WHERE c.category IS NOT NULL")
    List<String> findAllCategories();
}
