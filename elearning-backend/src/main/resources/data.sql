-- =============================================
-- Données initiales pour la plateforme E-Learning
-- =============================================

-- Catégories
INSERT INTO categories (id, name, description, icon) VALUES
(1, 'Programmation', 'Langages de programmation et développement logiciel', '💻'),
(2, 'Intelligence Artificielle', 'Machine Learning, Deep Learning et IA', '🤖'),
(3, 'Base de Données', 'SQL, NoSQL et administration de bases de données', '🗄️'),
(4, 'Développement Web', 'Frontend, Backend et Fullstack', '🌐'),
(5, 'Réseaux & Sécurité', 'Réseaux informatiques et cybersécurité', '🔒'),
(6, 'Mathématiques', 'Algèbre, Analyse et Statistiques', '📐'),
(7, 'DevOps', 'CI/CD, Docker, Kubernetes et Cloud', '⚙️'),
(8, 'Mobile', 'Développement Android, iOS et Flutter', '📱')
ON CONFLICT (id) DO NOTHING;

-- Admin user (password: admin123)
INSERT INTO users (id, first_name, last_name, email, password, role, created_at, active) VALUES
(1, 'Admin', 'System', 'admin@elearning.tn', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', NOW(), true)
ON CONFLICT (id) DO NOTHING;
