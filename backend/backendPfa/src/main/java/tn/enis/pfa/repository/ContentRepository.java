package tn.enis.pfa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Content;

import java.util.List;

public interface ContentRepository extends JpaRepository<Content, Long> {

    List<Content> findByModuleIdOrderByOrderIndexAsc(Long moduleId);
}
