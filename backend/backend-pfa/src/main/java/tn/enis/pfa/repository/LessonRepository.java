package tn.enis.pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Lesson;

public interface LessonRepository extends JpaRepository<Lesson, Long> {

}
