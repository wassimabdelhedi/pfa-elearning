package tn.enis.pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

}
