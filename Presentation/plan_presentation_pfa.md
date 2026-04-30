# Plan de Présentation PFA - LearnAgent (E-Learning Intelligent)

**Temps strict ciblé** : 15 minutes (Environ 40 à 45 secondes de temps de parole par slide).
**Répartition recommandée** : 
- **Donia** : Intro, Contexte, Solution, Web Features, Démos (~5 min)
- **Amin** : Architecture, Stack, Sécurité, Méthodologie (~5 min)
- **Wassim** : Moteur IA complet (NLP, Recommandation, Tuteur Gemini) (~5 min)

---

## Slide 1 : Page de Garde
* **Visuel** : Logo de l'université, Logo "LearnAgent".
* **Texte sur le slide** : 
  - Projet de Fin d'Année
  - Conception d'une Plateforme E-Learning Intelligente
  - Présenté par : Donia BAHLOUL, Amin FRIKHA, Wassim ABDELHEDI
  - Encadré par : [Nom de l'encadrant]
* **Speech (Donia)** : "Bonjour au jury. Nous avons l'honneur de vous présenter aujourd'hui LearnAgent. Je suis Donia, et avec mes collègues Amin et Wassim, nous allons vous exposer comment nous avons intégré l'IA au service de l'éducation."

## Slide 2 : Sommaire
* **Visuel** : Icônes thématiques pour chaque section.
* **Texte sur le slide** :
  1. Contexte & Problématique
  2. Solution : L'E-Learning Intelligent
  3. Méthodologie & Fonctionnalités Web
  4. Le Moteur d'Intelligence Artificielle
  5. Architectures & Technologies
  6. Démonstration & Bilan
* **Speech (Donia)** : "Notre présentation suivra ce cheminement : du contexte général à la solution proposée, en passant par les fonctionnalités web et IA, pour finir par l'architecture technique et une démonstration."

## Slide 3 : Contexte Général & Problématique
* **Visuel** : Infographie (Importance de l'éducation vs Barrières classiques).
* **Texte sur le slide** :
  - **Éducation** : Pilier fondamental du développement humain.
  - **Système Classique** : Rigide, passif, manque de personnalisation.
  - **Conséquences** : Décrochage, isolement et perte de motivation.
* **Speech (Donia)** : "L'éducation est le moteur de notre société, mais le modèle classique montre ses limites. La passivité des élèves et le manque de suivi personnalisé mènent trop souvent au décrochage scolaire."

## Slide 4 : Solution Proposée : L'E-Learning
* **Visuel** : "LearnAgent" au centre de l'écosystème numérique.
* **Texte sur le slide** :
  - **Apprentissage en ligne (E-Learning)** : Flexibilité et accessibilité.
  - **LearnAgent** : Un assistant proactif pour l'apprenant.
  - **Objectif** : Transformer la passivité en engagement.
* **Speech (Donia)** : "Pour répondre à ces défis, nous proposons LearnAgent. En misant sur l'E-learning ou apprentissage en ligne, nous brisons les barrières géographiques et temporelles tout en offrant un accompagnement proactif."

## Slide 5 : Méthodologie & Planification
* **Visuel** : Cycle Scrum (5 Sprints).
* **Texte sur le slide** :
  - **Approche Scrum** : 5 Sprints.
  - Développement itératif et incrémental.
  - Focus : Sécurité $\to$ Moteur de cours $\to$ IA.
* **Speech (Amin)** : "Le projet a été mené en méthode Scrum sur 5 sprints. Cette approche nous a permis d'assurer une progression constante, en validant d'abord le socle technique avant d'intégrer les modules d'intelligence artificielle."

## Slide 6 : Fonctionnalités de l'Application Web
* **Visuel** : Icônes (Cours, Quiz, Messagerie, Dashboard).
* **Texte sur le slide** :
  - **Gestion Pédagogique** : Cours hiérarchisés et quiz de validation.
  - **Messagerie & Alertes** : Chat temps réel et notifications SMTP.
  - **Suivi de Progression** : Tableaux de bord intuitifs.
* **Speech (Donia)** : "L'application offre un environnement complet : des cours structurés déblocables par des quiz, une messagerie instantanée pour briser l'isolement, et des outils de suivi précis pour l'étudiant comme pour l'enseignant."

## Slide 7 : Fonctionnalités IA - Extraction et NLP
* **Visuel** : Schéma (Fichier PDF/Word $\to$ Analyse NLP $\to$ Mots-clés).
* **Texte sur le slide** :
  - Extraction automatisée du texte.
  - Nettoyage sémantique (Stop-words).
  - Indexation intelligente sans effort enseignant.
* **Speech (Wassim)** : "Côté IA, tout commence par l'extraction. Dès qu'un enseignant dépose un document, notre système en analyse le contenu sémantique et extrait automatiquement les concepts clés pour indexer le cours."

## Slide 8 : Fonctionnalités IA - Recommandation Sémantique
* **Visuel** : Graphique de similarité cosinus (Sentence-BERT).
* **Texte sur le slide** :
  - Modèle **Sentence-BERT**.
  - Analyse du sens profond (Vecteurs).
  - Recommandation personnalisée selon le profil.
* **Speech (Wassim)** : "Nous utilisons Sentence-BERT pour comprendre le sens réel des cours. Le moteur compare le profil de l'étudiant avec notre base de données pour lui recommander les ressources les plus pertinentes à son niveau."

## Slide 9 : Fonctionnalités IA - Détection des Lacunes
* **Visuel** : Graphique de performance (Mapping Questions $\to$ Concepts).
* **Texte sur le slide** :
  - Analyse fine des échecs aux quiz.
  - Identification des concepts non maîtrisés.
  - Classification de sévérité (Critique / Moyen).
* **Speech (Wassim)** : "En cas d'échec, le système ne se contente pas d'une note. Il identifie précisément les lacunes sémantiques en faisant le lien entre les questions ratées et les concepts du cours."

## Slide 10 : Fonctionnalités IA - Tuteur Virtuel (Google Gemini)
* **Visuel** : Dialogue entre l'étudiant et Gemini.
* **Texte sur le slide** :
  - Génération de feedbacks personnalisés.
  - Explications ciblées sur les lacunes critiques.
  - **Batch Processing** pour l'optimisation.
* **Speech (Wassim)** : "Enfin, Google Gemini intervient comme un tuteur personnel. Il analyse les erreurs de l'étudiant et génère une explication pédagogique sur-mesure pour l'aider à surmonter ses difficultés immédiates."

## Slide 11 : Architecture Globale
* **Visuel** : Diagramme (React $\leftrightarrow$ Spring Boot $\leftrightarrow$ FastAPI).
* **Texte sur le slide** :
  - **Architecture Microservices**.
  - Découplage des responsabilités.
  - Communication asynchrone (WebFlux).
* **Speech (Amin)** : "L'architecture repose sur trois piliers : un Frontend React réactif, un Backend Spring Boot robuste pour la logique métier, et un microservice Python dédié exclusivement aux calculs IA intensifs."

## Slide 12 : Stack Technologique
* **Visuel** : Logos (React, Spring Boot, Java, Python, FastAPI, PostgreSQL).
* **Texte sur le slide** :
  - **Frontend** : React 19, Vite, Recharts.
  - **Backend** : Java 21, Spring Boot.
  - **IA** : Python 3.12, FastAPI, PyTorch.
* **Speech (Amin)** : "Notre stack est moderne et performante : Java 21 pour le cœur du système, PostgreSQL pour la persistance des données, et l'écosystème Python pour la puissance de ses bibliothèques de machine learning."

## Slide 13 : Sécurité & Accès
* **Visuel** : Flux d'authentification JWT.
* **Texte sur le slide** :
  - Authentification sécurisée par **JWT**.
  - Chiffrement des données (BCrypt).
  - Contrôle d'accès basé sur les rôles (RBAC).
* **Speech (Amin)** : "La sécurité est transversale. Nous utilisons des tokens JWT pour l'authentification et BCrypt pour le hachage. Le système RBAC garantit que chaque utilisateur n'accède qu'aux fonctionnalités autorisées."

## Slide 14 : Architecture du Microservice IA
* **Visuel** : Schéma interne du service Python.
* **Texte sur le slide** :
  - Indépendance vis-à-vis du backend Java.
  - Pipeline NLP optimisé.
  - Intégration de l'API Gemini.
* **Speech (Wassim)** : "Le microservice IA est totalement autonome. Cela nous permet de faire évoluer les modèles d'intelligence artificielle ou de changer de fournisseur de LLM sans jamais impacter la stabilité de la plateforme principale."

## Slide 15 : Démonstration - Dashboard & Recommandation
* **Visuel** : Capture du Dashboard Étudiant.
* **Texte sur le slide** :
  - Vue d'ensemble de l'apprenant.
  - Recommandations IA affichées.
* **Speech (Donia)** : "Passons à la pratique. Voici l'espace étudiant : un tableau de bord clair où les recommandations du moteur IA apparaissent dès la connexion pour guider l'apprentissage."

## Slide 16 : Démonstration - Tuteur IA en Action
* **Visuel** : Capture d'un quiz avec feedback Gemini.
* **Texte sur le slide** :
  - Diagnostic des erreurs.
  - Explications générées par l'IA.
* **Speech (Donia)** : "Lorsqu'un étudiant rencontre une difficulté sur un quiz, le tuteur IA intervient immédiatement. Il lui explique ses erreurs de manière pédagogique, simulant la présence d'un professeur particulier."

## Slide 17 : Démonstration - Espace Enseignant
* **Visuel** : Capture de l'upload de cours et statistiques.
* **Texte sur le slide** :
  - Création de contenu simplifiée.
  - Suivi analytique des étudiants.
* **Speech (Donia)** : "L'enseignant dispose d'outils puissants pour créer des cours et suivre ses classes. L'IA gère l'analyse des documents en arrière-plan, lui laissant plus de temps pour l'interaction humaine."

## Slide 18 : Bilan & Défis
* **Visuel** : Icônes (Vitesse, Intégration, Résilience).
* **Texte sur le slide** :
  - Intégration multi-langages (Java/Python).
  - Optimisation des temps de réponse IA.
  - Gestion des flux de données.
* **Speech (Amin)** : "Le défi majeur a été de faire communiquer harmonieusement deux écosystèmes différents (Java et Python). Nous avons réussi à maintenir une fluidité totale grâce à des communications asynchrones optimisées."

## Slide 19 : Perspectives
* **Visuel** : Icônes (Mobile, Cloud, Gamification).
* **Texte sur le slide** :
  - Extension mobile (React Native).
  - Déploiement Cloud (Docker/Kubernetes).
  - Gamification avancée (Badges, Challenges).
* **Speech (Wassim)** : "Pour la suite, nous envisageons une version mobile, un déploiement Cloud pour la scalabilité, et l'ajout de mécaniques de gamification pour renforcer encore plus l'engagement des étudiants."

## Slide 20 : Conclusion
* **Visuel** : "Merci pour votre attention".
* **Texte sur le slide** :
  - Fin de la présentation.
  - Questions ?
* **Speech (Donia)** : "LearnAgent démontre que l'E-learning intelligent n'est plus une option, mais une nécessité. Nous vous remercions pour votre attention et sommes prêts pour vos questions."
