package tn.enis.pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Course;
import tn.enis.pfa.entity.User;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByTeacher(User teacher);

    List<Course> findByCategory(String category);
}
