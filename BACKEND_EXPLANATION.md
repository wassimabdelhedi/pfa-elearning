# Backend Architecture Deep Dive

This document provides a highly comprehensive breakdown of the Java Spring Boot Backend (`elearning-backend`), detailing every module, package, and file’s role within the system.

The application follows the **Layered (N-Tier) Architectural Pattern**, which enforces separation of concerns:
- **Presentation Layer** (`controller`, `dto`)
- **Business Layer** (`service`)
- **Data Access Layer** (`repository`, `model`)
- **Cross-Cutting Concerns** (`security`, `config`, `exception`)

---

## 1. Application Entry Point
- **`ElearningApplication.java`**: The bootstrap class. It contains the standard `public static void main(String[] args)` method annotated with `@SpringBootApplication`. It initializes the Spring Context, auto-configures the application based on dependencies (like setting up Tomcat and Hibernate), and starts the server.

---

## 2. Models (`com.pfa.elearning.model`)
This package contains the JPA (Java Persistence API) Entity classes. These classes directly map to database tables in PostgreSQL.
- **`Category.java`**: Classifies courses into domains (e.g., "Computer Science", "Arts").
- **`Course.java`**: The core entity representing a learning program. It holds metadata like title, description, instructor ID, and references to its category.
- **`CourseRating.java`**: Records student reviews (stars/comments) for specific courses.
- **`DifficultyLevel.java`**: An ENUM defining the difficulty of a course (e.g., BEGINNER, INTERMEDIATE, ADVANCED).
- **`Enrollment.java`**: A mapping/join entity tracking which `User` (Student) is participating in which `Course`.
- **`Exercise.java`**: Represents homework or practical assignments linked to a course.
- **`Quiz.java`**: Represents an assessment module for testing a student's knowledge after chapters/courses.
- **`QuizQuestion.java`**: Contains an individual question prompt, expected answers, and ties back to a specific `Quiz`.
- **`QuizResult.java`**: Tracks a student's score and statistics when they attempt a `Quiz`.
- **`Recommendation.java`**: Stores the AI-generated course suggestions tailored to individual students. 
- **`Role.java`**: An ENUM defining user privileges (e.g., STUDENT, INSTRUCTOR, ADMIN).
- **`SearchHistory.java`**: A tracking entity that logs a user's search queries. The AI uses this data to improve recommendations.
- **`User.java`**: Defines the user profiles in the system, storing credentials (email, hashed password), their `Role`, and personal details.

---

## 3. Repositories (`com.pfa.elearning.repository`)
Interfaces extending Spring Data JPA's `JpaRepository` or `CrudRepository`. These automatically generate SQL queries (via Hibernate) to interact with the entities.
- **`CategoryRepository.java`**: Queries categories.
- **`CourseRatingRepository.java`**: Queries ratings associated with courses to generate aggregate scores.
- **`CourseRepository.java`**: Manages queries to find courses based on ids, authors, or categories.
- **`EnrollmentRepository.java`**: Queries to see if a user has access to course materials.
- **`ExerciseRepository.java`**: Finds exercises specific to a module.
- **`QuizRepository.java`**: Fetches quizzes.
- **`QuizResultRepository.java`**: Retrieves past attempts of a student.
- **`RecommendationRepository.java`**: Fetches cached recommendations for a user.
- **`SearchHistoryRepository.java`**: Logs and pulls recent query strings.
- **`UserRepository.java`**: Validates login credentials and checks for duplicate emails.

---

## 4. Services (`com.pfa.elearning.service`)
This layer encapsulates the core **Business Logic**. Controllers call Services, and Services call Repositories.
- **`CourseService.java`**: Logic for managing course lifecycles. Makes sure instructors can only edit their own courses and manages course file references.
- **`EnrollmentService.java`**: Handles the transaction of a user joining or dropping a course.
- **`FileStorageService.java`**: Contains the IO logic to safely upload, store, delete, and retrieve physical files (like PDFs, PPTXs, Videos) out of the local `/uploads/` directory.
- **`RecommendationService.java`**: Serves as the bridge to the Python AI engine using Spring's reactive `WebClient`. It passes student preferences/history to Python and formats the results for the frontend to digest.
- **`SearchService.java`**: Parses complex queries, searches across course titles and descriptions, and ensures that user actions are accurately logged via `SearchHistoryRepository`.
- **`UserService.java`**: Handles administrative tasks over users, fetching profiles, or updating user meta-data distinct from primary Authentication.

---

## 5. Controllers (`com.pfa.elearning.controller`)
The API endpoints (`@RestController`) that interact directly with the frontend's Axios requests. They define the URL paths and mapped HTTP Methods (GET, POST, PUT, DELETE).
- **`AdminController.java`**: Protected routes only for admins. Used for dashboard metrics or mass-banning/managing users.
- **`AuthController.java`**: Exposes `/login` and `/register`. Reissues security tokens on login.
- **`CategoryController.java`**: Fetches category dropdown lists.
- **`CourseController.java`**: Enables fetching the course catalog and allows instructors to mutate courses.
- **`EnrollmentController.java`**: Simple trigger endpoints to join a class.
- **`ExerciseController.java`**: Serving assignment documents to students.
- **`QuizController.java`**: Validates a student's answer sheet when they submit a test.
- **`RecommendationController.java`**: Exposes the user's customized feed of courses.
- **`SearchController.java`**: Facilitates the frontend's search bar API.

---

## 6. Security (`com.pfa.elearning.security`)
Manages system safety using Spring Security and JSON Web Tokens (JWT).
- **`CustomUserDetailsService.java`**: Automatically triggered during login. It queries the `UserRepository` to verify the user exists and maps their `Role` to Spring Security "GrantedAuthorities".
- **`JwtAuthenticationFilter.java`**: A middleware tool (OncePerRequestFilter). On every HTTP request, it intercepts the call, checks the `Authorization` header for a Bearer token, validates the signature, and sets the user's security context so the rest of the application knows who is making the request.
- **`JwtTokenProvider.java`**: A cryptographic utility. It uses the `io.jsonwebtoken` library to mint new JWT tokens (with expiration times and algorithmic signatures) and decode incoming ones.

---

## 7. Configuration (`com.pfa.elearning.config`)
Classes annotated with `@Configuration`, loaded on system startup to define application behavior.
- **`CorsConfig.java`**: Bypasses restrictions from browsers by explicitly authorizing the React Frontend's URL/port to communicate with the Spring server. 
- **`SecurityConfig.java`**: The core ruleset for Spring Security. Disables CSRF (because JWTs are immune to it), declares which endpoints are totally public (e.g., `/auth/login`), blocks the rest, and tells the system to use the `JwtAuthenticationFilter`.
- **`WebConfig.java`**: Defines broader Spring MVC settings, resource handlers (like mapping the physical `/uploads` folder to a readable frontend URL URL path segment), or WebClient builder instructions.

---

## 8. Data Transfer Objects (`com.pfa.elearning.dto`)
A set of plain Java objects structured specifically for JSON serialization via Jackson. DTOs prevent exposing hidden database logic or circular references directly to the user.
- **`request/CourseRequest.java`**: How the frontend packs a new course form.
- **`request/LoginRequest.java`**: Holds `{ "email": "...", "password": "..."}`.
- **`request/RegisterRequest.java`**: User registration payload.
- **`request/SearchRequest.java`**: Payload grouping search string and filters.
- **`response/AuthResponse.java`**: Exposes the JWT token and basic user id/role to store in the React Context after login.
- **`response/CourseResponse.java`**: A polished subset of the `Course` model tailored for the frontend UI rendering.
- **`response/RecommendationResponse.java`**: Groups AI recommendations and "match score %" in a nice layout.
- **`response/SearchResponse.java`**: Wraps the resulting array from search algorithms.

---

## 9. Exception Handling (`com.pfa.elearning.exception`)
Manages errors gracefully instead of throwing ugly Java StackTraces directly to the React Client.
- **`GlobalExceptionHandler.java`**: Uses `@ControllerAdvice` to catch specific exceptions globally across all controllers and maps them back into standard, humanly-readable JSON responses.
- **`ResourceNotFoundException.java`**: Thrown (e.g., requesting a course ID that doesn't exist); mapped to an HTTP 404.
- **`UnauthorizedException.java`**: Thrown if security logic fails; implicitly mapped to an HTTP 401 or 403.
