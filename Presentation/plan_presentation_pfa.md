# Plan de Présentation PFA - LearnAgent (E-Learning Intelligent)

**Temps estimé** : 15 minutes max (Environ 40 à 45 secondes par slide).
**Répartition recommandée** : 
- **Donia** : Intro, Contexte, Frontend, Démos (5 min)
- **Amin** : Architecture, Backend Spring Boot, Sécurité, Messagerie (5 min)
- **Wassim** : Moteur IA, NLP, Recommandation, Tuteur Gemini (5 min)

---

## Slide 1 : Page de Garde
* **Visuel** : Logo officiel de l'université/école, Logo "LearnAgent".
* **Texte sur le slide** : 
  - Projet de Fin d'Année
  - Conception et Développement d'une Plateforme E-Learning Intelligente
  - Présenté par : Donia BAHLOUL, Amin FRIKHA, Wassim ABDELHEDI
  - Encadré par : [Nom de l'encadrant]
* **Speech (Donia)** : "Bonjour à tous, membres du jury. Nous sommes honorés de vous présenter aujourd'hui le fruit de notre Projet de Fin d'Année : LearnAgent, une plateforme e-learning intelligente de nouvelle génération. Je suis Donia, voici mes collègues Amin et Wassim, et nous allons vous guider à travers notre réalisation."

## Slide 2 : Contexte & Problématique
* **Visuel** : Trois icônes épurées (Étudiant confus, Graphique en baisse, Engrenage bloqué).
* **Texte sur le slide** :
  - **Passivité** : Catalogues de cours immenses sans guidage.
  - **Isolement** : Manque de retour immédiat face aux difficultés.
  - **Conséquence** : Taux d'abandon (décrochage) très élevé.
* **Speech (Donia)** : "Aujourd'hui, l'apprentissage en ligne souffre d'un problème majeur : la passivité. Les étudiants se retrouvent face à des catalogues immenses sans guidage. Ce manque de personnalisation et l'absence de retour immédiat lors des difficultés conduisent à des taux d'abandon très élevés."

## Slide 3 : Objectifs du Projet
* **Visuel** : Schéma circulaire cible (Étudiant au centre avec des flèches vers IA, Suivi, Personnalisation).
* **Texte sur le slide** :
  - Création d'une plateforme proactive : **LearnAgent**.
  - Recommandation sémantique de parcours.
  - Tutorat virtuel automatisé.
  - Suivi continu et engagement.
* **Speech (Donia)** : "Pour pallier cela, notre objectif avec LearnAgent était de créer une plateforme proactive. Une solution capable de s'adapter à l'étudiant, de lui recommander les bons contenus sémantiquement, et de lui fournir un tuteur virtuel pour l'accompagner à la moindre lacune."

## Slide 4 : Méthodologie & Planification
* **Visuel** : Cycle Scrum stylisé ou frise chronologique des 5 Sprints.
* **Texte sur le slide** :
  - **Approche Agile (Scrum)** : 5 Sprints itératifs.
  - Sprint 1 & 2 : Fondations et Sécurité (RBAC, JWT).
  - Sprint 3 : Moteur Pédagogique et Progression.
  - Sprint 4 : Moteur d'Intelligence Artificielle.
  - Sprint 5 : Messagerie et Notifications.
* **Speech (Donia)** : "Nous avons adopté la méthode agile Scrum, divisant notre travail en 5 Sprints. Cette approche itérative nous a permis de construire d'abord une base backend solide, puis les interfaces, pour enfin y greffer notre couche d'Intelligence Artificielle et de communication."

## Slide 5 : Architecture Globale (Microservices)
* **Visuel** : Diagramme architectural global (React -> Spring Boot -> FastAPI / PostgreSQL).
* **Texte sur le slide** :
  - **Architecture Orientée Microservices**.
  - Séparation stricte des responsabilités.
  - Traitements asynchrones (Non-bloquant).
* **Speech (Amin)** : "Je prends le relais sur la partie technique. Nous avons opté pour une architecture orientée microservices. Le Frontend communique via API REST avec notre Backend central, qui lui-même délègue les calculs lourds d'IA de manière asynchrone à un microservice indépendant."

## Slide 6 : Stack Technologique
* **Visuel** : Nuage de logos (React, Vite, Java, Spring Boot, PostgreSQL, Python, FastAPI, Sentence-BERT, Gemini).
* **Texte sur le slide** :
  - **Frontend** : React 19, Vite, TailwindCSS.
  - **Backend** : Java 21, Spring Boot 3, PostgreSQL, Spring Security.
  - **Moteur IA** : Python 3, FastAPI, Sentence-BERT, Google Gemini API.
* **Speech (Amin)** : "Cette architecture s'appuie sur une stack très moderne : Spring Boot pour la robustesse transactionnelle, React pour une expérience utilisateur fluide, et l'écosystème Python, incontournable pour le traitement du langage naturel et l'IA."

## Slide 7 : Sécurité & Gestion des Accès
* **Visuel** : Schéma du flux de sécurité (Login -> JWT -> API Sécurisée).
* **Texte sur le slide** :
  - Authentification par **JSON Web Tokens (JWT)**.
  - Mots de passe chiffrés (BCrypt).
  - **Contrôle d'accès (RBAC)** : Administrateur, Enseignant, Étudiant.
* **Speech (Amin)** : "La fondation du système repose sur une sécurité stricte. Nous avons implémenté une authentification par jetons JWT cryptés. Le contrôle d'accès basé sur les rôles garantit l'étanchéité totale et sécurisée des espaces étudiants, enseignants et administrateurs."

## Slide 8 : Moteur Pédagogique et Progression
* **Visuel** : Modèle entité-relation simplifié (Cours -> Chapitres -> Quiz). Symbole de cadenas entre chapitres.
* **Texte sur le slide** :
  - Structure hiérarchique : Cours multi-chapitres.
  - Contrôle strict de la progression.
  - Déblocage conditionné par la réussite aux quiz.
* **Speech (Amin)** : "La gestion de l'apprentissage est rigoureuse. Les cours sont structurés en chapitres séquentiels. Un étudiant ne peut débloquer le chapitre suivant qu'après avoir validé le quiz du chapitre précédent, assurant une vraie progression pédagogique persistée en base."

## Slide 9 : Architecture du Microservice IA
* **Visuel** : Le diagramme TikZ (Figure : Architecture du microservice Python FastAPI) du chapitre 5.
* **Texte sur le slide** :
  - **Service Indépendant** : Python FastAPI.
  - **Découplage** : Zéro impact sur les performances du backend Java.
  - **Modules clés** : Extraction (PDF/PPTX), NLP, Recommandation, Tuteur IA.
* **Speech (Wassim)** : "J'aborde à présent le cœur innovant du projet : le microservice IA. Développé en Python avec FastAPI, il est totalement découplé du backend Java. Il est composé de 4 modules vitaux : l'extraction textuelle, l'analyse NLP, le moteur de recommandation, et le tuteur Gemini."

## Slide 10 : Pipeline d'Extraction et NLP
* **Visuel** : Le schéma TikZ de l'extraction (Document PDF/DOCX -> Text Extractor -> NLP Processor -> Mots-clés JSON).
* **Texte sur le slide** :
  - Extraction de texte brut (PDF, DOCX, PPTX).
  - Pipeline NLP bilingue (Filtrage des stop-words).
  - Indexation automatique (Extraction des 20 meilleurs mots-clés).
* **Speech (Wassim)** : "Lorsqu'un professeur dépose un cours, notre service Python extrait automatiquement le texte brut. Ensuite, un pipeline NLP nettoie le texte et en extrait les mots-clés techniques. Cela permet une indexation automatique sans aucun effort manuel pour l'enseignant."

## Slide 11 : Recommandation Hybride Sémantique
* **Visuel** : Encadré esthétique de l'Algorithme de Recommandation Hybride (avec les codes couleurs).
* **Texte sur le slide** :
  - Modèle **Sentence-BERT** (Espace vectoriel à 384 dimensions).
  - **Similarité Cosinus** entre requête et cours.
  - **Scoring Hybride** : Bonus (Niveau, Titre) / Malus (Déjà inscrit).
* **Speech (Wassim)** : "Plutôt qu'une recherche par mots exacts, nous utilisons Sentence-BERT. Le système transforme la requête en vecteur mathématique et calcule la similarité cosinus avec les cours. Nous appliquons ensuite un scoring hybride pour bonifier les résultats selon le niveau ou pénaliser les cours déjà suivis."

## Slide 12 : Détection Automatique des Lacunes
* **Visuel** : Schéma TikZ du processus de détection des lacunes (Weak Topic Detector).
* **Texte sur le slide** :
  - Déclenchement automatique post-échec (Score < 60%).
  - Mapping sémantique : Assignation des questions aux chapitres.
  - Classification stricte : **Critique (>70%)**, **Moyen**, **Faible**.
* **Speech (Wassim)** : "Lorsqu'un étudiant échoue à un quiz, l'IA intervient. Notre algorithme mappe sémantiquement les questions ratées aux chapitres correspondants. Il calcule un taux d'erreur et classifie les lacunes en niveaux de sévérité (Critique, Moyen), ciblant exactement ce que l'étudiant doit réviser."

## Slide 13 : Tuteur IA (Google Gemini)
* **Visuel** : Le diagramme TikZ (Flux d'intégration du Tuteur IA Gemini).
* **Texte sur le slide** :
  - Intégration de LLM (Large Language Model).
  - **Batch Processing** : Optimisation réseau $O(n) \to O(1)$.
  - **Fallback Routing** : Résilience face aux limites d'API (HTTP 429).
* **Speech (Wassim)** : "Enfin, nous avons intégré l'API Google Gemini. Pour optimiser les quotas réseau, nous utilisons un 'Batch Processing', envoyant toutes les erreurs en une seule requête JSON. Si l'API est surchargée, un 'Fallback Routing' bascule vers un modèle de secours. L'étudiant reçoit alors une explication personnalisée, sans avoir la réponse directe."

## Slide 14 : Messagerie et Notifications
* **Visuel** : Le diagramme TikZ de l'architecture des communications (Emails SMTP & Chat In-App).
* **Texte sur le slide** :
  - **Asynchrone (SMTP)** : Tâches planifiées (Relances inactivité).
  - **Temps Réel (In-App)** : Chat interne Étudiant-Enseignant.
  - **Objectif** : Maximiser la rétention et l'engagement.
* **Speech (Amin)** : "Pour maintenir l'engagement, notre dernier sprint a implémenté un système de communication double. Des tâches planifiées Spring surveillent l'inactivité et envoient des relances par email. En parallèle, un module de chat interne permet un échange direct entre étudiants et professeurs."

## Slide 15 : Démonstration - Espace Étudiant
* **Visuel** : Capture d'écran du Dashboard Étudiant (Catalogue et recommandations IA).
* **Texte sur le slide** :
  - Interface Dashboard Apprenant.
  - Recommandations IA poussées en temps réel.
* **Speech (Donia)** : "Passons à la pratique. Voici l'espace étudiant. L'interface est moderne et réactive. Dès la connexion, le moteur IA pousse des recommandations personnalisées basées sur le niveau et les mots-clés de l'étudiant."

## Slide 16 : Démonstration - Tuteur IA en Action
* **Visuel** : Capture d'écran d'un Quiz raté, montrant le rapport de lacunes (en rouge) et le feedback de Gemini.
* **Texte sur le slide** :
  - Rapport des concepts critiques.
  - Feedback pédagogique généré par l'IA.
* **Speech (Donia)** : "Ici, on voit l'intervention du tuteur IA. L'étudiant a échoué. Immédiatement, le système lui signale les chapitres critiques à revoir et affiche l'explication générée par Gemini pour l'aider à comprendre son erreur de logique."

## Slide 17 : Démonstration - Espace Enseignant
* **Visuel** : Capture d'écran de l'interface Enseignant (Création de cours, statistiques).
* **Texte sur le slide** :
  - Création de cours simplifiée.
  - Extraction de texte invisible (Background).
  - Suivi de la progression globale.
* **Speech (Donia)** : "Côté enseignant, la création de cours est un jeu d'enfant. L'upload du fichier lance l'extraction de texte en arrière-plan. De plus, le professeur dispose de tableaux de bord pour suivre en direct les performances globales de ses classes."

## Slide 18 : Bilan Technique & Défis
* **Visuel** : 3 icônes (Réseau, Vitesse, Base de données).
* **Texte sur le slide** :
  - **Communication Inter-Services** : Spring Boot vers FastAPI (WebClient).
  - **Latence IA** : Résolue par le traitement par lots (Batch).
  - **Résilience** : Mécanismes de secours (Fallback).
* **Speech (Amin)** : "La réalisation de ce projet n'a pas été sans défis. Le plus grand a été d'assurer une communication fluide et non-bloquante entre Spring Boot et FastAPI, et de garantir un temps de réponse acceptable de l'IA via le traitement par lots."

## Slide 19 : Perspectives d'Évolution
* **Visuel** : 3 icônes (Serveur Cloud, Mobile, Manette de jeu).
* **Texte sur le slide** :
  - **Scalabilité** : Conteneurisation Docker / Kubernetes.
  - **Mobilité** : Application React Native.
  - **Gamification** : Système de badges et de classement.
* **Speech (Wassim)** : "Bien que totalement fonctionnel, LearnAgent peut évoluer. Nos prochaines étapes incluent la conteneurisation Docker pour un déploiement Cloud automatisé, la création d'une application mobile React Native, et l'ajout de badges de gamification."

## Slide 20 : Conclusion Générale
* **Visuel** : Texte "Merci de votre attention", Logos, Code QR vers la vidéo de démo ou le code.
* **Texte sur le slide** :
  - Bilan de la solution LearnAgent.
  - Place aux questions.
* **Speech (Donia)** : "En conclusion, LearnAgent prouve qu'en couplant une architecture robuste à l'intelligence artificielle, on peut transformer un LMS classique en un véritable compagnon éducatif proactif. Nous vous remercions pour votre attention et sommes à votre disposition pour vos questions."
