# LearnAgent - PFA E-Learning Project

## Project Overview
This project, named **LearnAgent**, is a personalized e-learning recommendation system built for a "Projet de Fin d'Études" (PFA - ENIS). It uses a modern microservices-inspired architecture combining a responsive frontend, a robust backend web server, and a dedicated AI Python engine to analyze documents and recommend content.

The system relies on three distinct main modules:
1. **Frontend (`elearning-frontend`)**: The user interface built with React + Vite.
2. **Backend (`elearning-backend`)**: The core application logic and database manager built with Java Spring Boot.
3. **AI Recommendation Engine (`ai-recommendation-engine`)**: The AI and NLP analysis module built with Python FastAPI.

---

## 1. Project Flow & Architecture

### High-Level Data Flow
1. **User Interaction**: The user (student/teacher) interacts with the React frontend through their web browser.
2. **API Requests**: The frontend sends REST API calls (via Axios) to the Spring Boot Backend.
3. **Data Management**: The backend validates requests, checks security (JWT tokens), and communicates with a PostgreSQL database via Spring Data JPA to fetch/store user, course, and enrollment data.
4. **AI Processing**: When learning recommendations or document processing are needed, the backend communicates with the Python AI Engine using `WebClient`. 
5. **Machine Learning Logic**: The AI FastAPI engine receives documents or text, uses `sentence-transformers` for embeddings and NLP algorithms, processes file formats (PDFs, PPTX, DOCX), computes similar courses/documents via `scikit-learn`/`numpy`, and returns the matches.
6. **Result Delivery**: The Spring Boot backend retrieves the recommendation results and forwards them formatting properly to the React frontend, which presents the UI to the user.

---

## 2. Frameworks & Technologies Used

### Frontend (`elearning-frontend`)
- **React (v19)**: Core UI framework.
- **Vite**: Ultra-fast build tool and development server.
- **React Router DOM**: For client-side routing and page navigation.
- **Axios**: For making HTTP requests to the backend.
- **React Icons**: For SVG iconography.
- **ESLint**: For code linting and quality.

### Backend (`elearning-backend`)
- **Java (v21)**: Core programming language.
- **Spring Boot (v3.2.5)**: Main application framework.
  - `spring-boot-starter-web`: For REST controllers.
  - `spring-boot-starter-data-jpa`: For database interaction (ORM with Hibernate).
  - `spring-boot-starter-security`: For robust authentication and authorization.
  - `spring-webflux`: Specifically for the `WebClient` to make non-blocking HTTP requests to the AI server.
- **PostgreSQL**: Relational database database engine.
- **JJWT**: For handling JSON Web Tokens (JWT) creation, signing, and verification.
- **Lombok**: To reduce boilerplate (getters/setters).

### AI Recommendation Engine (`ai-recommendation-engine`)
- **Python (v3)**: Core programming language.
- **FastAPI**: Exceptionally fast Python web framework for APIs.
- **Uvicorn**: High-performance ASGI server for running the FastAPI app.
- **Sentence Transformers**: State-of-the-art AI embedding model for generating text embeddings.
- **Scikit-Learn & Numpy**: For data manipulation and vector similarity calculations (e.g., Cosine Similarity).
- **Document Parsers**: `PyPDF2` for PDFs, `python-docx` for Word documents, `python-pptx` for PowerPoints.

---

## 3. Detailed File & Folder Explanation

### `/elearning-frontend` (React + Vite)
- `package.json`: Contains scripts and dependencies for the frontend.
- `vite.config.js`: Configuration for the Vite bundler.
- `src/`: Core source files for React.
  - `main.jsx`: Entry point for React. Mounts the root component to the DOM.
  - `App.jsx`: Main routing setup and layout wrapper component.
  - `index.css`: Global CSS stylesheets and utility classes.
  - `api/`: Reusable Axios interceptors and functions to contact the backend.
  - `components/`: Reusable UI elements (Buttons, Navbars, Sidebars, Cards).
  - `context/`: React context providers (e.g., AuthContext) for global state.
  - `pages/`: Page-level components corresponding to specific routes (e.g., Home, Dashboard, CourseList, Login).

### `/elearning-backend` (Spring Boot)
- `pom.xml`: Maven configuration file defining all dependencies and build steps.
- `src/main/resources/application.properties` (or yml): Server config, Database credentials, connection strings.
- `src/main/java/com/pfa/elearning/`: Java Base Package.
  - `ElearningApplication.java`: Main bootstrap class to start the Spring Boot Application.
  - `config/`: Configuration classes (e.g., WebConfig for CORS, Security configuration, AI Client configuration).
  - `controller/`: REST API endpoints. Defines the URL paths and delegates to services.
  - `dto/`: Data Transfer Objects. Objects used to shape requests/responses across the network without exposing internal DB models directly.
  - `exception/`: Global Exception Handlers that intercept system errors and return clean JSON error responses to the frontend.
  - `model/`: Entity classes mapping to database tables managed by JPA.
  - `repository/`: Spring Data JPA interfaces. Provides automatic CRUD database queries.
  - `security/`: Components defining the JWT filters, Authentication providers, and user details services.
  - `service/`: The "Heart" of the backend. Contains business logic.
- `uploads/`: Used for storing files uploaded by users/professors (like PDFs or PPTX files for courses).

### `/ai-recommendation-engine` (Python + FastAPI)
- `requirements.txt`: Python package manager dependencies listing.
- `app/`: Source package.
  - `main.py`: Entry point for the FastAPI server, defining app instance and registering routes.
  - `models/`: Database models or internal python structural models.
  - `routes/`: FastAPI endpoints handling the HTTP requests (similar to Controllers in Spring Boot).
  - `schemas/`: Pydantic models for incoming and outgoing data validation and serialization.
  - `services/`: The core AI business logic.
    - Model loading strategies.
    - Text extraction functions (PDF, word reading).
    - Embedding logic using `sentence-transformers`.
    - Recommendation functions utilizing similarity matrices.

---

## 4. How to Keep This File Updated

This file is intended to serve as a living document of the application's architecture. 

**Whenever a developer makes structural changes or adds new features, follow these rules:**
1. **New Services/Controllers/Routes**: If a significantly new feature block is added (e.g., a Real-Time Chat System), it must be added to the flow and folder structure sections.
2. **New Frameworks**: If a new library is added to the project (e.g., adding `Redis` for caching or `Kafka` for messaging), update the **Frameworks & Technologies Used** section immediately.
3. **Database Changes**: If you switch databases or introduce NoSQL storage (like MongoDB) for specific data shapes, update the backend toolchain.

> **Instruction for AI Agents**: Every time you fulfill a prompt that structurally changes the architecture, adds a new library to any of the package files (`pom.xml`, `package.json`, `requirements.txt`), or significantly modifies the data flow, **you must automatically write an update to this `PROJECT_EXPLANATION.md` file** reflecting your changes before completing the task.
