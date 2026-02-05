-- Schéma PostgreSQL pour la plateforme e-learning
-- Généré pour référence ; JPA utilise ddl-auto=update en dev

-- Table users
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table courses
CREATE TABLE IF NOT EXISTS courses (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(255) NOT NULL,
    teacher_id BIGINT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table modules
CREATE TABLE IF NOT EXISTS modules (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    order_index INTEGER NOT NULL,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE
);

-- Table contents
CREATE TABLE IF NOT EXISTS contents (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    body TEXT,
    video_url VARCHAR(500),
    order_index INTEGER NOT NULL,
    module_id BIGINT NOT NULL REFERENCES modules(id) ON DELETE CASCADE
);

-- Table exercises
CREATE TABLE IF NOT EXISTS exercises (
    id BIGSERIAL PRIMARY KEY,
    question TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    correct_answer TEXT,
    options_json TEXT,
    order_index INTEGER NOT NULL,
    module_id BIGINT NOT NULL REFERENCES modules(id) ON DELETE CASCADE
);

-- Table enrollments
CREATE TABLE IF NOT EXISTS enrollments (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    course_id BIGINT NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed BOOLEAN DEFAULT FALSE,
    UNIQUE(user_id, course_id)
);

-- Table progress
CREATE TABLE IF NOT EXISTS progress (
    id BIGSERIAL PRIMARY KEY,
    enrollment_id BIGINT NOT NULL REFERENCES enrollments(id) ON DELETE CASCADE,
    content_id BIGINT REFERENCES contents(id) ON DELETE SET NULL,
    exercise_id BIGINT REFERENCES exercises(id) ON DELETE SET NULL,
    completed BOOLEAN,
    score DOUBLE PRECISION,
    completed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Table recommendations
CREATE TABLE IF NOT EXISTS recommendations (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    course_id BIGINT,
    content_id BIGINT,
    module_id BIGINT,
    reason VARCHAR(500),
    score DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index utiles
CREATE INDEX IF NOT EXISTS idx_courses_teacher ON courses(teacher_id);
CREATE INDEX IF NOT EXISTS idx_courses_category ON courses(category);
CREATE INDEX IF NOT EXISTS idx_enrollments_user ON enrollments(user_id);
CREATE INDEX IF NOT EXISTS idx_enrollments_course ON enrollments(course_id);
CREATE INDEX IF NOT EXISTS idx_progress_enrollment ON progress(enrollment_id);
CREATE INDEX IF NOT EXISTS idx_recommendations_user ON recommendations(user_id);
