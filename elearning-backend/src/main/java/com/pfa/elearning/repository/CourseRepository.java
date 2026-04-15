package com.pfa.elearning.repository;

import com.pfa.elearning.model.Course;
import com.pfa.elearning.model.DifficultyLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByPublishedTrue();
    List<Course> findByTeacherId(Long teacherId);
    List<Course> findByCategoryId(Long categoryId);
    List<Course> findByLevel(DifficultyLevel level);
    List<Course> findByPublishedTrueAndCategoryId(Long categoryId);

    @Query("SELECT c FROM Course c WHERE c.published = true AND " +
           "(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.keywords) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Course> searchByKeyword(@Param("keyword") String keyword);

    @Query(value = "SELECT c.* FROM courses c LEFT JOIN enrollments e ON c.id = e.course_id WHERE c.published = true GROUP BY c.id ORDER BY count(e.id) DESC LIMIT 10", nativeQuery = true)
    List<Course> findTopEnrolledCourses();

    @Query(value = "SELECT c.* FROM courses c LEFT JOIN categories cat ON c.category_id = cat.id WHERE c.published = true AND " +
           "(c.level = :level OR cat.name ILIKE CONCAT('%', :domain, '%') OR c.title ILIKE CONCAT('%', :objectif, '%') OR c.description ILIKE CONCAT('%', :objectif, '%')) LIMIT 10", nativeQuery = true)
    List<Course> findPersonalizedCourses(@Param("level") String level, @Param("domain") String domain, @Param("objectif") String objectif);

    @Query("SELECT c FROM Course c WHERE c.published = true AND c.id IN :ids")
    List<Course> findPublishedByIds(@Param("ids") List<Long> ids);

    long countByTeacherId(Long teacherId);
}
