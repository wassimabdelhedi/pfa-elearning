package tn.enis.pfa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import tn.enis.pfa.dto.RecommendationDto;
import tn.enis.pfa.entity.*;
import tn.enis.pfa.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    public List<RecommendationDto> getForUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        List<Recommendation> stored = recommendationRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, 20));
        if (!stored.isEmpty()) {
            return stored.stream().map(this::toDto).collect(Collectors.toList());
        }
        return computeRecommendations(userId);
    }

    private List<RecommendationDto> computeRecommendations(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        List<RecommendationDto> result = new ArrayList<>();

        List<Enrollment> enrollments = enrollmentRepository.findByUser(user);
        List<String> categories = enrollments.stream()
                .map(e -> e.getCourse().getCategory())
                .distinct()
                .toList();

        for (String category : categories) {
            List<Course> sameCategory = courseRepository.findByCategory(category);
            for (Course c : sameCategory) {
                boolean alreadyEnrolled = enrollments.stream().anyMatch(e -> e.getCourse().getId().equals(c.getId()));
                if (!alreadyEnrolled) {
                    Recommendation r = Recommendation.builder()
                            .user(user)
                            .type(Recommendation.RecommendationType.COURSE)
                            .courseId(c.getId())
                            .reason("Basé sur votre intérêt pour la catégorie : " + category)
                            .score(0.8)
                            .build();
                    r = recommendationRepository.save(r);
                    RecommendationDto dto = toDto(r);
                    dto.setCourseTitle(c.getTitle());
                    result.add(dto);
                    if (result.size() >= 5) break;
                }
            }
            if (result.size() >= 5) break;
        }

        if (result.size() < 3) {
            courseRepository.findAll().stream()
                    .filter(c -> enrollments.stream().noneMatch(e -> e.getCourse().getId().equals(c.getId())))
                    .limit(3 - result.size())
                    .forEach(c -> {
                        Recommendation r = Recommendation.builder()
                                .user(user)
                                .type(Recommendation.RecommendationType.COURSE)
                                .courseId(c.getId())
                                .reason("Cours populaire")
                                .score(0.5)
                                .build();
                        r = recommendationRepository.save(r);
                        RecommendationDto dto = toDto(r);
                        dto.setCourseTitle(c.getTitle());
                        result.add(dto);
                    });
        }

        return result;
    }

    private RecommendationDto toDto(Recommendation r) {
        RecommendationDto dto = RecommendationDto.from(r);
        if (r.getCourseId() != null) {
            courseRepository.findById(r.getCourseId()).ifPresent(c -> dto.setCourseTitle(c.getTitle()));
        }
        return dto;
    }
}
