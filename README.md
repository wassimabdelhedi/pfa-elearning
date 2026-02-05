# Plateforme E-Learning

Plateforme e-learning avec suivi apprenants/enseignants et moteur de recommandation (règles, sans IA). Stack : **Spring Boot**, **React**, **PostgreSQL**.

## Fonctionnalités

- **Authentification** : inscription / connexion (apprenant ou enseignant), JWT
- **Catalogue de cours** : liste, filtrage par catégorie, détail cours (modules, contenus, exercices)
- **Inscriptions** : l’apprenant s’inscrit à un cours et suit sa progression (contenus vus, scores exercices)
- **Tableau de bord** : mes inscriptions (apprenant), espace enseignant (mes cours)
- **Recommandations** : parcours et contenus suggérés selon le profil (règles : catégorie, cours populaires)
- **Enseignants** : création de cours (titre, description, catégorie), modules, contenus (lesson/vidéo), exercices (QCM/ouvert)

## Prérequis

- Java 17+
- Node.js 18+
- PostgreSQL
- Maven

## Base de données

1. Créer une base PostgreSQL (ex. `pfa_db`).
2. Optionnel : exécuter le schéma manuellement :
   ```bash
   psql -U postgres -d pfa_db -f database/schema.sql
   ```
3. Sinon, le backend utilise `spring.jpa.hibernate.ddl-auto=update` pour créer/mettre à jour les tables.

Configurer l’URL, l’utilisateur et le mot de passe dans `backend/backend-pfa/src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pfa_db
spring.datasource.username=postgres
spring.datasource.password=root
```

## Backend (Spring Boot)

```bash
cd backend/backend-pfa
./mvnw spring-boot:run
```

API disponible sur **http://localhost:8081/api** (contexte `/api`).

Comptes créés au premier démarrage (DataLoader) :

- **Enseignant** : `enseignant@elearning.com` / `password`
- **Apprenant** : `apprenant@elearning.com` / `password`

## Frontend (React + Vite)

```bash
cd frontend
npm install
npm run dev
```

Application sur **http://localhost:5173**. Le proxy Vite redirige `/api` vers le backend (port 8081).

## Structure du projet

- **backend/backend-pfa** : API REST (entités, repositories, services, contrôleurs), JWT, CORS
- **database** : `schema.sql` (schéma PostgreSQL)
- **frontend** : React, React Router, Axios, pages Login/Register, Dashboard, Catalogue, Détail cours, Recommandations, Mes cours (enseignant)

## API principales

| Méthode | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Inscription |
| POST | `/auth/login` | Connexion (retourne JWT) |
| GET | `/courses` | Liste des cours (option : `?category=...`) |
| GET | `/courses/{id}` | Détail d’un cours |
| GET | `/courses/teacher/{id}` | Cours d’un enseignant |
| POST | `/courses` | Créer un cours (header `Authorization: Bearer <token>`) |
| POST | `/enrollments/courses/{courseId}` | S’inscrire à un cours |
| GET | `/enrollments/me` | Mes inscriptions |
| GET | `/progress/enrollment/{id}` | Progression d’une inscription |
| POST | `/progress/enrollment/{id}` | Enregistrer progression (contenu vu / exercice) |
| GET | `/recommendations/me` | Recommandations pour l’utilisateur connecté |

Toutes les routes protégées nécessitent le header : `Authorization: Bearer <token>`.
