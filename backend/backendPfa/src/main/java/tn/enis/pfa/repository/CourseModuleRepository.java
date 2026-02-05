package tn.enis.pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.CourseModule;

import java.util.List;

public interface CourseModuleRepository extends JpaRepository<CourseModule, Long> {

    List<CourseModule> findByCourseIdOrderByOrderIndexAsc(Long courseId);
}
