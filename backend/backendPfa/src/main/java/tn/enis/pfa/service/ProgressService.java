package tn.enis.pfa.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.enis.pfa.dto.ProgressDto;
import tn.enis.pfa.dto.ProgressRequest;
import tn.enis.pfa.entity.*;
import tn.enis.pfa.repository.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProgressService {

    private final ProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ContentRepository contentRepository;
    private final ExerciseRepository exerciseRepository;

    public List<ProgressDto> findByEnrollment(Long enrollmentId) {
        return progressRepository.findByEnrollmentId(enrollmentId).stream()
                .map(ProgressDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProgressDto recordProgress(Long userId, Long enrollmentId, ProgressRequest request) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId).orElseThrow(() -> new IllegalArgumentException("Inscription non trouvée"));
        if (!enrollment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Non autorisé");
        }
        Progress progress;
        if (request.getContentId() != null) {
            Content content = contentRepository.findById(request.getContentId()).orElseThrow();
            Optional<Progress> existing = progressRepository.findByEnrollmentIdAndContentId(enrollmentId, request.getContentId());
            if (existing.isPresent()) {
                progress = existing.get();
                progress.setCompleted(request.getCompleted() != null ? request.getCompleted() : true);
            } else {
                progress = Progress.builder()
                        .enrollment(enrollment)
                        .content(content)
                        .completed(request.getCompleted() != null ? request.getCompleted() : true)
                        .build();
                progress = progressRepository.save(progress);
            }
        } else if (request.getExerciseId() != null) {
            Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow();
            Optional<Progress> existing = progressRepository.findByEnrollmentIdAndExerciseId(enrollmentId, request.getExerciseId());
            if (existing.isPresent()) {
                progress = existing.get();
                progress.setScore(request.getScore());
                progress.setCompleted(request.getCompleted() != null ? request.getCompleted() : true);
            } else {
                progress = Progress.builder()
                        .enrollment(enrollment)
                        .exercise(exercise)
                        .score(request.getScore())
                        .completed(request.getCompleted() != null ? request.getCompleted() : true)
                        .build();
                progress = progressRepository.save(progress);
            }
        } else {
            throw new IllegalArgumentException("contentId ou exerciseId requis");
        }
        return ProgressDto.from(progress);
    }
}
