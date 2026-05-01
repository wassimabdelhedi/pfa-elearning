# Guide de Préparation au Jury - LearnAgent

Ce document est conçu pour vous aider à répondre aux questions techniques du jury. Il synthétise les choix technologiques et le fonctionnement interne du projet.

---

## 1. Le Backend (Spring Boot / Java)

### Pourquoi Spring Boot ?
- **Robustesse** : Idéal pour les applications d'entreprise et les systèmes transactionnels (gestion des cours, quiz, utilisateurs).
- **Écosystème** : Spring Security pour le JWT, Spring Data JPA pour la base de données.
- **Scalabilité** : Facilite la création de microservices et l'intégration avec d'autres systèmes.

### Points Techniques Clés :
- **Sécurité (JWT)** : Nous utilisons des **JSON Web Tokens**. Pourquoi ? Parce qu'ils sont sans état (stateless), ce qui évite de stocker des sessions sur le serveur et facilite la scalabilité.
- **Contrôle d'Accès (RBAC)** : Les rôles (Étudiant, Enseignant, Admin) sont vérifiés à chaque requête via des annotations `@PreAuthorize`.
- **Gestion de la Progression** : Le moteur pédagogique vérifie si le quiz précédent est validé (score >= 60%) avant d'autoriser l'accès au chapitre suivant via une logique métier dans le service `CourseService`.
- **Communication Inter-services** : Pour parler au moteur IA (Python), le backend Java utilise **WebClient** (Spring WebFlux) pour envoyer des requêtes asynchrones et ne pas bloquer le thread principal.

---

## 2. Le Moteur IA (FastAPI / Python)

### Pourquoi un microservice séparé en Python ?
- **Bibliothèques** : Python est le langage roi de l'IA (PyTorch, Transformers, LangChain).
- **Indépendance** : Si l'IA consomme trop de RAM, elle ne fait pas planter le serveur Java qui gère les paiements ou les inscriptions.

### Fonctionnement des Algorithmes :
- **Extraction & NLP** : Nous utilisons des bibliothèques comme `PyPDF2` ou `python-docx` pour extraire le texte. Le NLP nettoie les "stop-words" (le, la, de) pour ne garder que les mots porteurs de sens.
- **Sentence-BERT (SBERT)** : C'est le cœur de la recommandation. SBERT transforme un texte en un **vecteur** (une liste de nombres) qui représente son "sens".
- **Similarité Cosinus** : Pour recommander un cours, on compare le vecteur du profil de l'étudiant avec les vecteurs des cours. Plus l'angle entre les vecteurs est petit, plus les sujets sont proches.
- **Détection des Lacunes** : En analysant les questions ratées, le système identifie les chapitres liés sémantiquement à ces questions.

---

## 3. L'Intégration de Google Gemini (LLM)

### Quel est le rôle de Gemini ?
Gemini n'est pas utilisé pour tout, car cela coûterait trop cher et serait lent. Il intervient uniquement comme **Tuteur Virtuel** pour expliquer des erreurs spécifiques.

### Techniques utilisées :
- **Prompt Engineering** : Nous envoyons à Gemini le contexte (le chapitre, la question ratée, la réponse de l'étudiant) avec une instruction stricte : "Explique pédagogiquement pourquoi cette réponse est fausse sans donner directement la solution".
- **Batch Processing** : Si un étudiant rate 5 questions, on ne fait pas 5 appels à l'API. On regroupe tout dans une seule requête pour gagner du temps et réduire les coûts.

---

## 4. Questions "Pièges" Possibles & Réponses

**Q : Pourquoi ne pas avoir tout fait en Java ou tout en Python ?**
*R : Java est excellent pour la gestion métier et la sécurité, Python est imbattable pour l'IA. Utiliser les deux permet de tirer le meilleur de chaque monde (Architecture Microservices).*

**Q : Comment gérez-vous la latence de l'IA (qui est lente) ?**
*R : Nous utilisons des traitements asynchrones. L'utilisateur n'attend pas que l'IA finisse pour naviguer sur le site ; les recommandations se mettent à jour en arrière-plan.*

**Q : Votre système est-il RGPD ?**
*R : Oui, les mots de passe sont hachés avec **BCrypt** et nous ne stockons que le strict nécessaire. Les données envoyées à Gemini sont anonymisées.*

**Q : Que se passe-t-il si l'API Gemini est hors-ligne ?**
*R : Nous avons prévu un **Fallback mechanism**. Le système affiche un message d'erreur poli ou renvoie vers les ressources de cours classiques au lieu de faire planter l'application.*

---

## 5. Synthèse des Données
- **Base de données** : PostgreSQL (hébergée sur Supabase).
- **Stockage Fichiers** : Les documents de cours sont stockés de manière sécurisée (Cloud ou dossier local indexé).
- **Vitesse** : Frontend React 19 + Vite pour une interface ultra-rapide.
