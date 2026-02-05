package tn.enis.pfa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enis.pfa.dto.EnrollmentDto;
import tn.enis.pfa.entity.Course;
import tn.enis.pfa.entity.Enrollment;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.repository.CourseRepository;
import tn.enis.pfa.repository.EnrollmentRepository;
import tn.enis.pfa.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public List<EnrollmentDto> findByUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        return enrollmentRepository.findByUser(user).stream()
                .map(EnrollmentDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentDto enroll(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new IllegalArgumentException("Cours non trouvé"));
        if (enrollmentRepository.existsByUserAndCourse(user, course)) {
            throw new IllegalArgumentException("Déjà inscrit à ce cours");
        }
        Enrollment enrollment = Enrollment.builder()
                .user(user)
                .course(course)
                .completed(false)
                .build();
        enrollment = enrollmentRepository.save(enrollment);
        return EnrollmentDto.from(enrollment);
    }

    public boolean isEnrolled(Long userId, Long courseId) {
        User user = userRepository.findById(userId).orElse(null);
        Course course = courseRepository.findById(courseId).orElse(null);
        if (user == null || course == null) return false;
        return enrollmentRepository.existsByUserAndCourse(user, course);
    }
}
