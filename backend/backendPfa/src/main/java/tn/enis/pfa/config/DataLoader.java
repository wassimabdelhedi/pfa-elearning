package tn.enis.pfa.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.enis.pfa.entity.Content;
import tn.enis.pfa.entity.Course;
import tn.enis.pfa.entity.CourseModule;
import tn.enis.pfa.entity.Exercise;
import tn.enis.pfa.entity.User;
import tn.enis.pfa.repository.*;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final CourseModuleRepository courseModuleRepository;
    private final ContentRepository contentRepository;
    private final ExerciseRepository exerciseRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) return;

        User teacher = User.builder()
                .email("enseignant@elearning.com")
                .passwordHash(passwordEncoder.encode("password"))
                .fullName("Ahmed Enseignant")
                .role(User.Role.TEACHER)
                .build();
        teacher = userRepository.save(teacher);

        User learner = User.builder()
                .email("apprenant@elearning.com")
                .passwordHash(passwordEncoder.encode("password"))
                .fullName("Fatima Apprenante")
                .role(User.Role.LEARNER)
                .build();
        learner = userRepository.save(learner);

        Course c1 = Course.builder()
                .title("Introduction à Spring Boot")
                .description("Découvrez Spring Boot pour créer des API REST.")
                .category("Développement")
                .teacher(teacher)
                .build();
        c1 = courseRepository.save(c1);

        CourseModule m1 = CourseModule.builder().title("Les bases").orderIndex(0).course(c1).build();
        m1 = courseModuleRepository.save(m1);
        contentRepository.save(Content.builder().title("Qu'est-ce que Spring ?").type(Content.ContentType.LESSON).body("Spring est un framework Java...").orderIndex(0).module(m1).build());
        contentRepository.save(Content.builder().title("Vidéo : Premier projet").type(Content.ContentType.VIDEO).videoUrl("https://example.com/video1").orderIndex(1).module(m1).build());
        exerciseRepository.save(Exercise.builder().question("Quel type d'application Spring Boot permet de créer ?").type(Exercise.ExerciseType.QCM).correctAnswer("REST").optionsJson("[\"REST\",\"Desktop\",\"Mobile\"]").orderIndex(0).module(m1).build());

        CourseModule m2 = CourseModule.builder().title("API REST").orderIndex(1).course(c1).build();
        m2 = courseModuleRepository.save(m2);
        contentRepository.save(Content.builder().title("Controllers").type(Content.ContentType.LESSON).body("Les contrôleurs exposent les endpoints...").orderIndex(0).module(m2).build());

        Course c2 = Course.builder()
                .title("React : les fondamentaux")
                .description("Apprenez React pour le frontend.")
                .category("Frontend")
                .teacher(teacher)
                .build();
        c2 = courseRepository.save(c2);
        CourseModule m3 = CourseModule.builder().title("Composants").orderIndex(0).course(c2).build();
        m3 = courseModuleRepository.save(m3);
        contentRepository.save(Content.builder().title("JSX et composants").type(Content.ContentType.LESSON).body("React utilise JSX...").orderIndex(0).module(m3).build());
    }
}
