package com.pfa.elearning.service;

import com.pfa.elearning.model.Chapter;
import com.pfa.elearning.model.Course;
import com.pfa.elearning.model.Quiz;
import com.pfa.elearning.model.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendInactivityEmail(User student) {
        Context context = new Context();
        context.setVariable("name", student.getFirstName());
        sendHtmlEmail(student.getEmail(), "We Miss You at LearnAgent!", "emails/inactivity", context);
    }

    @Async
    public void sendPerfectQuizScoreEmail(User teacher, User student, Quiz quiz) {
        Context context = new Context();
        context.setVariable("teacherName", teacher.getFirstName());
        context.setVariable("studentName", student.getFullName());
        context.setVariable("quizTitle", quiz.getTitle());
        sendHtmlEmail(teacher.getEmail(), "Perfect Score Alert! 🎉", "emails/perfect-score", context);
    }

    @Async
    public void sendEnrollmentNotification(String studentEmail, String studentFirstName, String courseName, String teacherFullName, Long courseId) {
        Context context = new Context();
        context.setVariable("studentName", studentFirstName);
        context.setVariable("courseName", courseName);
        context.setVariable("teacherName", teacherFullName);
        context.setVariable("courseUrl", "http://localhost:5173/courses/" + courseId);
        sendHtmlEmail(studentEmail, "Welcome to " + courseName, "emails/enrollment", context);
    }

    @Async
    public void sendEnrollmentNotificationToTeacher(String teacherEmail, String teacherFirstName, String studentFullName, String studentEmail, String courseName) {
        Context context = new Context();
        context.setVariable("teacherName", teacherFirstName);
        context.setVariable("studentName", studentFullName);
        context.setVariable("studentEmail", studentEmail);
        context.setVariable("courseName", courseName);
        sendHtmlEmail(teacherEmail, "New Student Enrolled in \"" + courseName + "\"", "emails/enrollment-teacher", context);
    }

    @Async
    public void sendCourseSuggestionEmail(User student, List<Course> suggestions) {
        if (suggestions == null || suggestions.isEmpty()) return;
        Context context = new Context();
        context.setVariable("name", student.getFirstName());
        context.setVariable("suggestions", suggestions);
        sendHtmlEmail(student.getEmail(), "Your Weekly AI Recommendations", "emails/suggestions", context);
    }

    @Async
    public void sendCourseMatchedInterestEmail(User student, Course course) {
        Context context = new Context();
        context.setVariable("studentName", student.getFirstName());
        context.setVariable("courseTitle", course.getTitle());
        context.setVariable("interestDomain", student.getDomaineInteret());
        context.setVariable("courseUrl", "http://localhost:5173/courses/" + course.getId());
        sendHtmlEmail(student.getEmail(), "New Course Matching Your Interests!", "emails/new-course-interest", context);
    }

    @Async
    public void sendLessonUpdatedEmail(User student, Course course, Chapter lesson) {
        Context context = new Context();
        context.setVariable("studentName", student.getFirstName());
        context.setVariable("lessonTitle", lesson.getTitle());
        context.setVariable("courseTitle", course.getTitle());
        context.setVariable("courseUrl", "http://localhost:5173/courses/" + course.getId());
        sendHtmlEmail(student.getEmail(), "Lesson Content Updated: " + lesson.getTitle(), "emails/lesson-updated", context);
    }

    @Async
    public void sendPasswordResetEmail(String toEmail, String firstName, String resetUrl) {
        Context context = new Context();
        context.setVariable("name", firstName);
        context.setVariable("resetUrl", resetUrl);
        sendHtmlEmail(toEmail, "Password Reset Request - LearnAgent", "emails/reset-password", context);
    }

    private void sendHtmlEmail(String to, String subject, String templateName, Context context) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            String htmlContent = templateEngine.process(templateName, context);
            
            helper.setFrom(senderEmail != null && !senderEmail.isEmpty() ? senderEmail : "noreply@learnagent.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            log.info("Email sent successfully to {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}", to, e);
        }
    }
}
