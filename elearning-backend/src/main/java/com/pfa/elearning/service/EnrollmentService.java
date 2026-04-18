package com.pfa.elearning.service;

import com.pfa.elearning.exception.ResourceNotFoundException;
import com.pfa.elearning.model.*;
import com.pfa.elearning.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final QuizRepository quizRepository;
    private final QuizResultRepository quizResultRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseCompletionRepository exerciseCompletionRepository;

    @Transactional
    public Enrollment enrollStudent(User student, Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", courseId));

        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new IllegalArgumentException("You are already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .progressPercentage(0.0)
                .completed(false)
                .build();

        return enrollmentRepository.save(enrollment);
    }

    public List<Enrollment> getStudentEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    public List<Enrollment> getInProgressEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentIdAndCompletedFalse(studentId);
    }

    @Transactional
    public Enrollment updateProgress(Long enrollmentId, double progress, User student) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));

        if (!enrollment.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("You can only update your own enrollment");
        }

        enrollment.setProgressPercentage(Math.min(progress, 100.0));

        if (progress >= 100.0 && !enrollment.isCompleted()) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
        }

        return enrollmentRepository.save(enrollment);
    }

    @Transactional
    public Enrollment updateProgressByCourseId(Long courseId, double progress, User student) {
        Enrollment enrollment = enrollmentRepository.findByStudentId(student.getId()).stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "courseId", courseId));

        enrollment.setProgressPercentage(Math.min(progress, 100.0));

        if (progress >= 100.0 && !enrollment.isCompleted()) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
        }

        return enrollmentRepository.save(enrollment);
    }

    public boolean isEnrolled(Long studentId, Long courseId) {
        return enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId);
    }

    public Map<String, Double> calculateDetailedProgress(Enrollment enrollment) {
        Long courseId = enrollment.getCourse().getId();
        Long studentId = enrollment.getStudent().getId();

        // 1. Chapters Progress
        List<Chapter> courseChapters = chapterRepository.findByCourseIdOrderByChapterOrderAsc(courseId);
        int totalChapters = courseChapters.size();
        long completedChapters = chapterProgressRepository.findByEnrollmentId(enrollment.getId())
                .stream().filter(cp -> cp.isCompleted()).count();
        double chaptersProgress = totalChapters == 0 ? 100.0 : (completedChapters * 100.0) / totalChapters;

        // 2. Quizzes Progress
        List<Quiz> courseQuizzes = quizRepository.findByCourseId(courseId);
        int totalQuizzes = courseQuizzes.size();
        Set<Long> courseQuizIds = courseQuizzes.stream().map(Quiz::getId).collect(java.util.stream.Collectors.toSet());
        long completedQuizzes = quizResultRepository.findByStudentId(studentId).stream()
                .filter(qr -> courseQuizIds.contains(qr.getQuiz().getId()))
                .map(qr -> qr.getQuiz().getId())
                .distinct()
                .count();
        double quizzesProgress = totalQuizzes == 0 ? 100.0 : (completedQuizzes * 100.0) / totalQuizzes;

        // 3. Exercises Progress
        List<Exercise> courseExercises = exerciseRepository.findByCourseId(courseId);
        int totalExercises = courseExercises.size();
        Set<Long> courseExerciseIds = courseExercises.stream().map(Exercise::getId).collect(java.util.stream.Collectors.toSet());
        long completedExercises = exerciseCompletionRepository.findByStudentId(studentId).stream()
                .filter(ec -> courseExerciseIds.contains(ec.getExercise().getId()))
                .count();
        double exercisesProgress = totalExercises == 0 ? 100.0 : (completedExercises * 100.0) / totalExercises;

        Map<String, Double> progressDetails = new HashMap<>();
        progressDetails.put("chaptersProgress", Math.min(chaptersProgress, 100.0));
        progressDetails.put("quizzesProgress", Math.min(quizzesProgress, 100.0));
        progressDetails.put("exercisesProgress", Math.min(exercisesProgress, 100.0));
        
        double overallProgress = (chaptersProgress + quizzesProgress + exercisesProgress) / 3.0;
        progressDetails.put("overallProgress", Math.min(overallProgress, 100.0));

        return progressDetails;
    }

    @Transactional
    public Enrollment updateEnrollmentStatus(Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", "id", enrollmentId));
        
        Map<String, Double> progress = calculateDetailedProgress(enrollment);
        
        double chapters = progress.get("chaptersProgress");
        double quizzes = progress.get("quizzesProgress");
        double exercises = progress.get("exercisesProgress");
        
        // The course is completed ONLY if ALL three are at 100%
        boolean isFinished = (chapters >= 100.0 && quizzes >= 100.0 && exercises >= 100.0);
        
        enrollment.setProgressPercentage(progress.get("overallProgress"));
        
        if (isFinished && !enrollment.isCompleted()) {
            enrollment.setCompleted(true);
            enrollment.setCompletedAt(LocalDateTime.now());
        } else if (!isFinished && enrollment.isCompleted()) {
            // In case a new chapter/quiz/exercise was added to the course later
            enrollment.setCompleted(false);
            enrollment.setCompletedAt(null);
        }
        
        return enrollmentRepository.save(enrollment);
    }

    public boolean isChaptersFinished(Enrollment enrollment) {
        Long courseId = enrollment.getCourse().getId();
        long totalChapters = chapterRepository.findByCourseIdOrderByChapterOrderAsc(courseId).size();
        if (totalChapters == 0) return true;

        long completedChapters = chapterProgressRepository.findByEnrollmentId(enrollment.getId())
                .stream().filter(ChapterProgress::isCompleted).count();

        return completedChapters >= totalChapters;
    }
}
