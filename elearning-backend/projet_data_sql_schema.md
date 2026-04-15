Voici toutes les informations, l'architecture métier et technique du projet E-Learning pour générer un fichier `data.sql` riche, cohérent et sans erreurs de clés étrangères.

### 1️⃣ Les Entités et Colonnes (Schéma PostgreSQL)

Le projet tourne sur Spring Boot 3 + PostgreSQL.

*   **`users`** : `id`, `full_name`, `email`, `password`, `role` (VARCHAR), `active` (BOOLEAN), `created_at` (TIMESTAMP).
    *   *Champs spécifiques étudiant* : `niveau`, `domaine_interet`, `autre_domaine_interet`, `objectif`, `autre_objectif` (tous en VARCHAR).
*   **`categories`** : `id`, `name`, `description`, `icon` (VARCHAR).
*   **`courses`** : `id`, `title`, `description` (TEXT), `level` (VARCHAR), `category_id`, `teacher_id`, `keywords`, `published` (BOOLEAN), `created_at` (TIMESTAMP), `updated_at` (TIMESTAMP).
*   **`chapters`** : `id`, `title`, `content` (TEXT), `chapter_order`, `support_type` (VARCHAR), `file_url`, `course_id`.
*   **`exercises`** : `id`, `title`, `description` (TEXT), `content` (TEXT), `original_file_name`, `file_storage_path`, `level` (VARCHAR), `course_id`, `teacher_id`, `published` (BOOLEAN).
*   **`quizzes`** : `id`, `title`, `description` (TEXT), `level` (VARCHAR), `course_id`, `teacher_id`, `published` (BOOLEAN).
*   **`quiz_questions`** : `id`, `quiz_id`, `text` (VARCHAR), `options` (ARRAY de VARCHAR ou bien une table collection `quiz_question_options`), `correct_answer` (INTEGER, qui est l'index de la bonne réponse).
*   **`enrollments`** : `id`, `student_id`, `course_id`, `progress` (FLOAT), `completed` (BOOLEAN), `enrolled_at`, `completed_at`.
*   **`quiz_results`** : `id`, `student_id`, `quiz_id`, `score` (INTEGER), `total_questions` (INTEGER), `percentage` (FLOAT), `submitted_at`.

---

### 2️⃣ Les Relations entre Entités (Associations JPA)

Pour respecter les clés étrangères lors de l'insertion (l'ordre d'insertion est crucial) :

1.  **Users / Categories** : Indépendants, à insérer en premier.
2.  Un **Course** appartient à ✅ *1 Category* (`category_id`) et à ✅ *1 User (Enseignant)* (`teacher_id`).
3.  Un **Chapter** appartient à ✅ *1 Course* (`course_id`).
4.  Un **Exercise** appartient à ✅ *1 Course* (`course_id`) et ✅ *1 User (Enseignant)* (`teacher_id`).
5.  Un **Quiz** appartient à ✅ *1 Course* (`course_id`) et ✅ *1 User (Enseignant)* (`teacher_id`).
6.  Une **Question** appartient à ✅ *1 Quiz* (`quiz_id`). (Attention la liste de choix "options" doit être gérée correctement en SQL array syntax ou liée, en JPA via `@ElementCollection`).
7.  Un **Enrollment** lie ✅ *1 User (Student)* et ✅ *1 Course*.
8.  Un **QuizResult** lie ✅ *1 User (Student)* et ✅ *1 Quiz*.

---

### 3️⃣ Le type de données à insérer (Contenu Réel)

**Langue** : 100% en Français.
**Domaine global** : Informatique de haut niveau et Ingénierie (Web, IA, Data, Software).
**Quantités demandées** :
*   **Utilisateurs** : Insérer 1 Admin, 2 Enseignants, 4 Étudiants (dont certains ont des profils Différents (ex: niveau=Débutant, domaine_interet=IA)). 
    *   *Hash BCrypt Fixe à utiliser pour `password`* : `$2a$10$wN18OQOa/S4.qR./F5h8CeMweY0.A23tqJOWXStZ/0q20nL/T1s0y` (Ce hash brut équivaut au mot de passe : `password` en clair).
*   **Catégories** : 4 Catégories (ex: "Développement Web", "Data Science & IA", "DevOps & Cloud", "Cybersécurité").
*   **Cours** : Environ 5 à 6 cours. 
*   **Chapitres** : ~2 à 3 chapitres par cours avec un contenu textuel intéressant.
*   **Quiz** : 1 quiz par cours en moyenne (avec 2 à 3 questions).
*   **Enrollments & Résultats** : Quelques lignes simulant que les étudiants ont déjà commencé ou terminé des cours et passé des quiz avec différents pourcentages de réussite.

---

### 4️⃣ Le niveau pédagogique visé

Différencier pour avoir un mixte complet. L'attribut `level` dans les cours/quiz/exercices prend strictement l'une de ces 3 valeurs (ENUM Java) :
*   `BEGINNER`
*   `INTERMEDIATE`
*   `ADVANCED`

---

### 5️⃣ Contraintes techniques & Enumérations

*   **Identifiants (IDs)** : Mettre les IDs en dur (1, 2, 3...) dans vos requêtes `INSERT` pour faciliter le mapping des clés étrangères !
*   **Séquences** : Comme le système est en `@GeneratedValue(strategy = GenerationType.IDENTITY)`, veuillez obligatoirement inclure à la toute fin de votre script les commandes de mise à jour des séquences. (exemple : `SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));`). Faire ça pour *toutes* les tables.
*   **Role (ENUM)** : L'attribut `role` prend : `'STUDENT'`, `'TEACHER'`, `'ADMIN'`.
*   **SupportType (ENUM)** : L'attribut `support_type` dans `chapters` prend : `'VIDEO'`, `'TEXT'`, `'PDF'`, `'IMAGE'`.
*   Les champs booléens (ex: `published`, `active`, `completed`) utiliseront `true`/`false`.
*   Dates : Utiliser `CURRENT_TIMESTAMP` ou le format standard `YYYY-MM-DD HH:MI:SS`.
