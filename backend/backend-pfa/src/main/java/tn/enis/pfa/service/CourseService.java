package tn.enis.pfa.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tn.enis.pfa.entity.Course;
import tn.enis.pfa.entity.Enrollment;
import tn.enis.pfa.entity.Progress;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.exception.ResourceNotFoundException;
import tn.enis.pfa.repository.CourseRepository;
import tn.enis.pfa.repository.EnrollmentRepository;
import tn.enis.pfa.repository.ProgressRepository;
import tn.enis.pfa.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProgressRepository progressRepository;

    public Course saveCourse(Course course, String teacherUsername) {
        User teacher = userRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found"));

        course.setTeacher(teacher);
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public void enroll(Long courseId, String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(LocalDateTime.now());

        enrollmentRepository.save(enrollment);
    }

    public List<Progress> getStudentProgress(String username) {
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return progressRepository.findByStudent(student);
    }
}
