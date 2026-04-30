# Plan de Présentation PFA - LearnAgent (E-Learning Intelligent)

**Temps strict ciblé** : 13 minutes (Environ 30 à 35 secondes de temps de parole par slide, laissant le temps de respirer et pointer les schémas).
**Répartition recommandée** : 
- **Donia** : Intro, Contexte, Frontend, Démos (~4 min 20s)
- **Amin** : Architecture, Backend Spring Boot, Sécurité, Messagerie (~4 min 20s)
- **Wassim** : Moteur IA, NLP, Recommandation, Tuteur Gemini (~4 min 20s)

---

## Slide 1 : Page de Garde
* **Visuel** : Logo de l'université, Logo "LearnAgent".
* **Texte sur le slide** : 
  - Projet de Fin d'Année
  - Conception d'une Plateforme E-Learning Intelligente
  - Présenté par : Donia BAHLOUL, Amin FRIKHA, Wassim ABDELHEDI
  - Encadré par : [Nom de l'encadrant]
* **Speech (Donia)** : "Bonjour au jury. Nous avons l'honneur de vous présenter aujourd'hui LearnAgent, notre plateforme e-learning intelligente. Je suis Donia, et avec mes collègues Amin et Wassim, nous allons vous exposer notre travail."

## Slide 2 : Contexte & Problématique
* **Visuel** : Trois icônes épurées (Étudiant confus, Graphique en baisse, Engrenage bloqué).
* **Texte sur le slide** :
  - **Passivité** : Catalogues immenses sans guidage.
  - **Isolement** : Aucun retour immédiat lors des difficultés.
  - **Bilan** : Taux d'abandon très élevé.
* **Speech (Donia)** : "L'e-learning actuel est trop souvent passif. Les étudiants font face à des listes de cours sans réel guidage. Ce manque de personnalisation et d'accompagnement provoque de l'isolement et un fort taux d'abandon."

## Slide 3 : Objectifs du Projet
* **Visuel** : Schéma cible (Étudiant au centre avec IA, Suivi, Personnalisation).
* **Texte sur le slide** :
  - Apprentissage proactif : **LearnAgent**.
  - Recommandation sémantique.
  - Tutorat virtuel automatisé.
* **Speech (Donia)** : "Notre réponse est LearnAgent. Une plateforme proactive qui s'adapte à l'étudiant via des recommandations sémantiques ciblées, et qui intègre un tuteur IA pour l'accompagner dès qu'il rencontre une difficulté."

## Slide 4 : Méthodologie & Planification
* **Visuel** : Cycle Scrum (5 Sprints).
* **Texte sur le slide** :
  - **Approche Scrum** : 5 Sprints.
  - S1-S3 : Fondations, Sécurité, Moteur de cours.
  - S4 : Intelligence Artificielle.
  - S5 : Messagerie.
* **Speech (Donia)** : "Le projet a été mené en méthode Scrum sur 5 sprints. Nous avons d'abord bâti un socle backend robuste et l'interface, avant d'y intégrer notre moteur d'Intelligence Artificielle puis les outils de communication."

## Slide 5 : Architecture Globale
* **Visuel** : Diagramme architectural global (React -> Spring Boot -> FastAPI).
* **Texte sur le slide** :
  - **Microservices**.
  - Séparation des responsabilités.
  - Traitements asynchrones.
* **Speech (Amin)** : "Côté technique, nous avons opté pour une architecture orientée microservices. Le Frontend React communique avec le Backend Spring Boot, qui délègue de manière asynchrone les calculs complexes à un service Python indépendant."

## Slide 6 : Stack Technologique
* **Visuel** : Logos (React, Spring Boot, PostgreSQL, Python, FastAPI, Gemini).
* **Texte sur le slide** :
  - **Frontend** : React 19, Vite.
  - **Backend** : Java 21, Spring Boot, PostgreSQL.
  - **Moteur IA** : Python 3, FastAPI, Sentence-BERT, Gemini.
* **Speech (Amin)** : "Notre stack est à l'état de l'art : React pour la réactivité client, Spring Boot et PostgreSQL pour la robustesse transactionnelle, et l'écosystème Python avec Sentence-BERT pour le traitement IA."

## Slide 7 : Sécurité & Accès
* **Visuel** : Schéma du flux JWT.
* **Texte sur le slide** :
  - Authentification **JWT** (JSON Web Tokens).
  - Mots de passe chiffrés (BCrypt).
  - Contrôle d'accès strict (RBAC).
* **Speech (Amin)** : "La sécurité est fondamentale. Elle est assurée par des tokens JWT cryptés en BCrypt. Notre contrôle d'accès (RBAC) isole parfaitement les espaces administrateur, enseignant et étudiant."

## Slide 8 : Moteur Pédagogique
* **Visuel** : Entité-relation simplifié (Cours -> Chapitres -> Quiz avec cadenas).
* **Texte sur le slide** :
  - Cours hiérarchisés.
  - Progression conditionnée.
  - Déblocage via succès aux quiz.
* **Speech (Amin)** : "Le moteur pédagogique impose une progression rigoureuse. Les cours sont divisés en chapitres. Un étudiant ne débloque le chapitre suivant qu'après validation du quiz précédent, garantissant son assiduité."

## Slide 9 : Architecture du Microservice IA
* **Visuel** : Schéma de l'architecture Python (main.py, recommender, nlp_processor, etc.).
* **Texte sur le slide** :
  - **Service Indépendant** (Python FastAPI).
  - **Zéro impact** sur le backend Java.
  - Extraction, NLP, Recommandation, Tuteur.
* **Speech (Wassim)** : "Le cœur de l'innovation est notre microservice IA en Python. Découplé de Java pour ne pas impacter ses performances, il gère l'extraction des documents, le NLP, la recommandation et le tuteur Gemini."

## Slide 10 : Extraction et NLP
* **Visuel** : Schéma d'extraction (PDF/DOCX -> NLP -> Mots-clés).
* **Texte sur le slide** :
  - Extraction de texte brut.
  - Filtrage NLP (Stop-words).
  - Indexation automatique des mots-clés.
* **Speech (Wassim)** : "Lorsqu'un enseignant dépose un fichier PDF ou Word, l'IA en extrait le texte brut, filtre les mots inutiles et indexe automatiquement les mots-clés métiers. L'enseignant n'a rien à configurer."

## Slide 11 : Recommandation Sémantique
* **Visuel** : Encadré de l'Algorithme de Recommandation Hybride.
* **Texte sur le slide** :
  - Modèle **Sentence-BERT** (Vecteurs).
  - Similarité Cosinus.
  - Scoring hybride (Bonus/Malus).
* **Speech (Wassim)** : "Pour la recommandation, nous utilisons Sentence-BERT qui analyse le sens profond par similarité cosinus. Nous y appliquons un scoring hybride qui pénalise les cours déjà vus et bonifie la pertinence du niveau."

## Slide 12 : Détection des Lacunes
* **Visuel** : Schéma du Weak Topic Detector.
* **Texte sur le slide** :
  - Post-échec (Score < 60%).
  - Mapping sémantique (Questions $\to$ Chapitres).
  - Sévérité : **Critique (>70%)**, Moyen, Faible.
* **Speech (Wassim)** : "En cas d'échec à un quiz, l'IA cartographie sémantiquement les erreurs vers les chapitres du cours. Elle calcule le taux d'échec par concept et classifie la lacune en niveau Critique, Moyen ou Faible."

## Slide 13 : Tuteur IA (Google Gemini)
* **Visuel** : Flux d'intégration du Tuteur IA Gemini.
* **Texte sur le slide** :
  - Explication pédagogique ciblée.
  - **Batch Processing** (Optimisation réseau).
  - **Fallback Routing** (Haute disponibilité).
* **Speech (Wassim)** : "Pour corriger ces lacunes, Gemini intervient comme tuteur virtuel. Nous regroupons les erreurs en 'Batch' pour optimiser le réseau, et utilisons un mécanisme de secours automatique en cas de surcharge des serveurs Google."

## Slide 14 : Messagerie & Notifications
* **Visuel** : Architecture des communications (SMTP & In-App).
* **Texte sur le slide** :
  - **Asynchrone** : Alertes emails (SMTP) pour l'inactivité.
  - **Temps Réel** : Chat interne Étudiant-Enseignant.
* **Speech (Amin)** : "Pour maximiser l'engagement, nous avons couplé des relances emails automatiques via SMTP pour contrer l'inactivité, avec un chat interne en temps réel facilitant l'échange direct entre l'étudiant et le professeur."

## Slide 15 : Démonstration - Espace Étudiant
* **Visuel** : Capture du Dashboard Étudiant (Recommandations).
* **Texte sur le slide** :
  - Interface Dashboard Apprenant.
  - Recommandations dynamiques.
* **Speech (Donia)** : "Place à l'interface. Voici le Dashboard Étudiant, conçu de manière fluide et réactive. Dès la connexion, le moteur IA y pousse instantanément des cours adaptés au profil de l'apprenant."

## Slide 16 : Démonstration - Tuteur IA
* **Visuel** : Capture d'un Quiz raté, affichage des lacunes et du feedback Gemini.
* **Texte sur le slide** :
  - Alerte sur les concepts critiques.
  - Feedback IA personnalisé.
* **Speech (Donia)** : "Si l'étudiant échoue, voici le tableau de bord du tuteur IA. Le système lui signale les concepts critiques à revoir absolument, et affiche l'explication générée sur-mesure par Gemini."

## Slide 17 : Démonstration - Espace Enseignant
* **Visuel** : Capture de la création de cours / statistiques.
* **Texte sur le slide** :
  - Création de cours rapide.
  - Traitement IA invisible.
  - Suivi des classes.
* **Speech (Donia)** : "Côté enseignant, la création de cours et l'upload de documents sont extrêmement simplifiés. L'extraction par l'IA se fait en arrière-plan sans bloquer l'interface, et l'enseignant garde un œil sur les statistiques globales."

## Slide 18 : Bilan & Défis
* **Visuel** : 3 icônes (Réseau, Vitesse, Base de données).
* **Texte sur le slide** :
  - Communication Inter-Services (WebClient).
  - Latence IA (Résolue par traitement par lots).
  - Résilience système.
* **Speech (Amin)** : "Le grand défi technique de ce projet fut la communication inter-services. Nous l'avons résolu via le client asynchrone Spring WebFlux et le traitement par lots pour éviter toute latence ressentie par l'utilisateur."

## Slide 19 : Perspectives
* **Visuel** : 3 icônes (Cloud Docker, Mobile React Native, Gamification).
* **Texte sur le slide** :
  - **Scalabilité** : Déploiement Docker / Cloud.
  - **Mobilité** : App React Native.
  - **Gamification** : Badges et classements.
* **Speech (Wassim)** : "Pour l'avenir, LearnAgent pourrait évoluer vers une application mobile React Native, inclure des mécanismes de gamification avec des badges, et être intégralement déployé via Docker sur le Cloud."

## Slide 20 : Conclusion
* **Visuel** : "Merci de votre attention" + Code QR (optionnel).
* **Texte sur le slide** :
  - Fin de la présentation.
  - Questions ?
* **Speech (Donia)** : "LearnAgent prouve qu'une architecture microservices robuste couplée à l'IA transforme l'e-learning. Nous vous remercions pour votre attention et sommes à l'écoute de vos questions."
