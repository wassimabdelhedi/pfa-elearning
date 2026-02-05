package tn.enis.pfa.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.enis.pfa.entity.Recommendation;
import tn.enis.pfa.entity.User;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

    List<Recommendation> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
}
