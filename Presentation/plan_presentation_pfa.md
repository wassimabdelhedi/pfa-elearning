# Plan de Présentation PFA - LearnAgent (E-Learning Intelligent)

**Temps estimé** : 15 minutes max (Environ 40 à 45 secondes par slide).
**Répartition recommandée** : 
- **Donia** : Intro, Contexte, Frontend, Démos (5 min)
- **Amin** : Architecture, Backend Spring Boot, Sécurité, Messagerie (5 min)
- **Wassim** : Moteur IA, NLP, Recommandation, Tuteur Gemini (5 min)

---

## Slide 1 : Page de Garde
* **Visuel** : Logo officiel de l'université/école, Logo "LearnAgent", Titre du projet : "Conception et Développement d'une Plateforme E-Learning Intelligente", Noms des membres de l'équipe, Nom de l'encadrant, Année universitaire.
* **Speech (Donia)** : "Bonjour à tous, membres du jury. Nous sommes honorés de vous présenter aujourd'hui le fruit de notre Projet de Fin d'Année : LearnAgent, une plateforme e-learning intelligente de nouvelle génération. Je suis Donia, voici mes collègues Amin et Wassim, et nous allons vous guider à travers notre réalisation."

## Slide 2 : Contexte & Problématique
* **Visuel** : Trois icônes représentant les problèmes actuels : Un étudiant confus (Passivité), une flèche en baisse (Taux d'abandon élevé), un moule unique (Manque de personnalisation).
* **Speech (Donia)** : "Aujourd'hui, l'apprentissage en ligne souffre d'un problème majeur : la passivité. Les étudiants se retrouvent face à des catalogues de cours immenses sans guidage. Ce manque de personnalisation et l'absence de retour immédiat lors des difficultés conduisent à des taux d'abandon très élevés."

## Slide 3 : Objectifs du Projet
* **Visuel** : Schéma cible montrant l'étudiant au centre, entouré de : Apprentissage adaptatif, Recommandation intelligente, Tutorat IA, et Suivi temps réel.
* **Speech (Donia)** : "Pour pallier cela, notre objectif avec LearnAgent était de créer une plateforme proactive. Une solution capable de s'adapter à l'étudiant, de lui recommander les bons contenus sémantiquement, et de lui fournir un tuteur virtuel pour l'accompagner à la moindre lacune."

## Slide 4 : Méthodologie & Planification
* **Visuel** : Cycle Scrum stylisé montrant les 5 Sprints (S1: Auth, S2: Utilisateurs, S3: Cours/Quiz, S4: Moteur IA, S5: Messagerie).
* **Speech (Donia)** : "Nous avons adopté la méthode agile Scrum, divisant notre travail en 5 Sprints. Cette approche itérative nous a permis de construire d'abord une base backend solide, puis les interfaces, pour enfin y greffer notre couche d'Intelligence Artificielle et de communication."

## Slide 5 : Architecture Globale (Microservices)
* **Visuel** : Diagramme architectural global (React en front, Spring Boot au centre avec PostgreSQL, et FastAPI en bas).
* **Speech (Amin)** : "Je prends le relais sur la partie technique. Nous avons opté pour une architecture orientée microservices. Le Frontend en React communique via API REST avec notre Backend central en Spring Boot, qui lui-même délègue les calculs lourds d'IA de manière asynchrone à un microservice indépendant en Python FastAPI."

## Slide 6 : Stack Technologique
* **Visuel** : Nuage de logos bien espacés : React 19, Vite, Tailwind/CSS, Java 21, Spring Boot 3, PostgreSQL, Python, FastAPI, Sentence-BERT, Google Gemini.
* **Speech (Amin)** : "Cette architecture s'appuie sur une stack très moderne : Spring Boot pour la robustesse transactionnelle, React pour une expérience utilisateur fluide, et l'écosystème Python pour le traitement du langage naturel."

## Slide 7 : Sécurité & Gestion des Accès (Sprint 1 & 2)
* **Visuel** : Schéma du flux JWT (Login -> Token -> Requête sécurisée). Tableau simple des 3 rôles (Admin, Enseignant, Étudiant).
* **Speech (Amin)** : "La fondation du système repose sur une sécurité stricte. Nous avons implémenté une authentification par jetons JWT (JSON Web Tokens) cryptés avec BCrypt. Le contrôle d'accès basé sur les rôles (RBAC) garantit l'étanchéité totale des espaces étudiants, enseignants et administrateurs."

## Slide 8 : Moteur Pédagogique (Sprint 3)
* **Visuel** : Graphe ou modèle entité-relation simplifié : Cours -> Chapitres -> Ressources -> Quiz. Symbole de cadenas entre les chapitres.
* **Speech (Amin)** : "La gestion de l'apprentissage est rigoureuse. Les cours sont structurés en chapitres séquentiels. Un étudiant ne peut débloquer le chapitre suivant qu'après avoir validé le quiz du chapitre précédent, assurant ainsi une vraie progression pédagogique persistée en base de données."

## Slide 9 : Architecture du Microservice IA (Sprint 4)
* **Visuel** : Le schéma TikZ (Figure : Architecture du microservice Python FastAPI) que nous avons réalisé dans le rapport (main.py, recommender, nlp_processor, text_extractor).
* **Speech (Wassim)** : "J'aborde à présent le cœur innovant du projet : le microservice IA. Développé en Python avec FastAPI, il est totalement découplé du backend Java pour ne pas impacter ses performances. Il est composé de 4 modules vitaux : l'extraction textuelle, l'analyse NLP, le moteur de recommandation, et le tuteur Gemini."

## Slide 10 : Pipeline d'Extraction et NLP
* **Visuel** : Le schéma TikZ de l'extraction (Document PDF/DOCX -> Text Extractor -> NLP Processor -> Mots-clés JSON).
* **Speech (Wassim)** : "Lorsqu'un professeur dépose un cours, notre service Python extrait automatiquement le texte brut des PDF ou PPTX. Ensuite, un pipeline NLP nettoie le texte, filtre les mots vides, et en extrait les mots-clés techniques. Cela permet une indexation automatique sans effort pour l'enseignant."

## Slide 11 : Recommandation Hybride Sémantique
* **Visuel** : Diagramme ou encadré de l'algorithme de recommandation (Sentence-BERT + Cosinus + Ajustements).
* **Speech (Wassim)** : "Plutôt qu'une simple recherche par mots, nous utilisons Sentence-BERT. Le système transforme la requête de l'étudiant en un vecteur mathématique et calcule la similarité cosinus avec les cours. Nous appliquons ensuite un algorithme de scoring hybride pour bonifier les résultats selon le niveau de difficulté ou pénaliser les cours déjà suivis."

## Slide 12 : Détection des Lacunes Pédagogiques
* **Visuel** : Schéma du Weak Topic Detector (Question ratée -> Mapping Chapitre -> Seuil Critique > 70%).
* **Speech (Wassim)** : "Lorsqu'un étudiant échoue à un quiz, l'IA intervient. Notre algorithme mappe sémantiquement les questions ratées aux chapitres correspondants. Il calcule un taux d'erreur et classifie les lacunes en niveaux de sévérité (Critique, Moyen, Faible), ciblant exactement ce que l'étudiant doit réviser."

## Slide 13 : Tuteur IA (Google Gemini)
* **Visuel** : Le diagramme TikZ (Flux d'intégration du Tuteur IA Gemini) montrant le Batch Processing et le Fallback.
* **Speech (Wassim)** : "Enfin, nous avons intégré l'API Google Gemini. Pour optimiser les quotas réseau, nous utilisons un mécanisme de 'Batch Processing', envoyant toutes les erreurs en une seule requête JSON. Si l'API est surchargée, un système de 'Fallback Routing' bascule automatiquement vers un modèle de secours. L'étudiant reçoit alors une explication personnalisée, sans jamais avoir la réponse directe."

## Slide 14 : Système de Messagerie (Sprint 5)
* **Visuel** : Le diagramme de l'architecture des communications (Emails SMTP & Chat In-App).
* **Speech (Amin)** : "Pour maintenir l'engagement, notre dernier sprint a implémenté un système de notification double. Des tâches planifiées Spring surveillent l'inactivité et envoient des relances par email SMTP. En parallèle, un module de chat interne permet un échange direct entre étudiants et professeurs en temps réel."

## Slide 15 : Démonstration - Expérience Étudiant
* **Visuel** : Capture d'écran large et de haute qualité du Dashboard Étudiant montrant le catalogue et les recommandations.
* **Speech (Donia)** : "Passons à la pratique. Voici l'espace étudiant. L'interface est moderne et réactive. Dès la connexion, le moteur IA pousse des recommandations personnalisées basées sur le niveau et les mots-clés de l'étudiant."

## Slide 16 : Démonstration - Suivi et Tuteur IA
* **Visuel** : Capture d'écran du résultat d'un Quiz raté, montrant le rapport de lacunes et l'explication générée par Gemini.
* **Speech (Donia)** : "Ici, on voit l'intervention du tuteur IA. L'étudiant a échoué. Immédiatement, le système lui signale les chapitres critiques à revoir et affiche l'explication générée par Gemini pour l'aider à comprendre son erreur de logique."

## Slide 17 : Démonstration - Expérience Enseignant
* **Visuel** : Capture d'écran de l'espace Enseignant (Upload de cours, statistiques des étudiants).
* **Speech (Donia)** : "Côté enseignant, la création de cours est un jeu d'enfant. L'upload du fichier lance l'extraction de texte en arrière-plan. De plus, le professeur dispose de tableaux de bord pour suivre en direct les performances globales de ses classes."

## Slide 18 : Bilan Technique & Défis Relevés
* **Visuel** : 3 puces avec des icônes : Communication Inter-Services, Fiabilité IA, Temps de Réponse.
* **Speech (Amin)** : "La réalisation de ce projet n'a pas été sans défis. Le plus grand a été d'assurer une communication fluide entre Spring Boot et FastAPI, et de garantir un temps de réponse acceptable de l'IA (d'où notre implémentation du traitement par lots et du client asynchrone WebFlux)."

## Slide 19 : Perspectives d'Évolution
* **Visuel** : Icônes d'avenir (Cloud/Docker, Mobile React Native, Gamification).
* **Speech (Wassim)** : "Bien que totalement fonctionnel, LearnAgent peut encore évoluer. Nos prochaines étapes incluraient la conteneurisation Docker pour un déploiement Cloud automatisé, la création d'une application mobile React Native, et l'ajout de badges de gamification pour stimuler l'apprentissage."

## Slide 20 : Conclusion Générale
* **Visuel** : "Merci de votre attention", avec les logos de l'université. Eventuellement un lien/QR Code vers le dépôt Git.
* **Speech (Donia)** : "En conclusion, LearnAgent prouve qu'en couplant une architecture robuste à l'intelligence artificielle, on peut transformer un simple LMS en un véritable compagnon éducatif. Nous vous remercions pour votre attention et sommes à votre entière disposition pour répondre à vos questions."
