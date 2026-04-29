# Plan de Présentation Soutenance PFA : LearnAgent

Voici une proposition de plan structuré sur environ 15 slides pour votre soutenance de PFA. Ce plan respecte le déroulement chronologique de votre rapport et inclut des recommandations précises sur les captures d'écran à intégrer pour rendre la présentation dynamique.

---

## 1. Page de Garde
* **Contenu** : Titre du projet ("LearnAgent : Plateforme E-Learning Intelligente"), noms des membres de l'équipe (Wassim Abdelhedi, Donia Bahloul, Amin Frikha), encadrante (Mme. Masmoudi Sahla), année universitaire et logo de l'ENIS.
* **Visuel** : Logo de l'école et logo du projet LearnAgent.

## 2. Contexte et Problématique
* **Contenu** : Essor de l'e-learning, mais limites des plateformes classiques (perte de motivation, manque de personnalisation, absence de détection automatique des lacunes, parcours non restrictifs).
* **Visuel** : Icônes illustrant les problèmes (ex: étudiant confus, montagne de données).

## 3. Solution Proposée : LearnAgent
* **Contenu** : Présentation globale de la solution. Plateforme intelligente combinant un parcours d'apprentissage séquentiel strict, une évaluation automatisée, et un moteur de recommandation / tuteur par IA.
* **Visuel** : Schéma très simplifié (Étudiant -> Plateforme -> IA).

## 4. Méthodologie et Organisation (Scrum)
* **Contenu** : Choix de la méthode agile Scrum. Répartition de l'équipe (Product Owner, Scrum Master, Dev Team Frontend/Backend/IA). Découpage en 5 Sprints.
* **Visuel** : Diagramme du Product Backlog / Roadmap des 5 Sprints.

## 5. Architecture Globale du Système
* **Contenu** : Approche microservices orientée N-Tier. Frontend (React.js/Vite), Backend principal (Spring Boot/PostgreSQL), Service IA indépendant (Python FastAPI).
* **Visuel** : Diagramme d'architecture globale (Frontend <-> Backend <-> IA <-> Base de données).

## 6. Sprint 1 : Sécurité et Gestion des Utilisateurs
* **Contenu** : Implémentation du système d'authentification robuste (JWT, BCrypt) et contrôle d'accès basé sur les rôles (RBAC : Admin, Teacher, Student).
* **Visuel** : **[Capture d'écran 1]** Interface de Connexion / Inscription ou **[Capture d'écran 2]** Tableau de bord Administrateur (gestion des rôles).

## 7. Sprint 2 : Le Cœur Pédagogique (Moteur de Cours)
* **Contenu** : Gestion hiérarchique des cours (chapitres ordonnés, supports PDF/Vidéos/PPTX). 
* **Visuel** : **[Capture d'écran 3]** Interface de création de cours (Vue Enseignant) montrant la structuration en chapitres.

## 8. Sprint 2 (Suite) : Suivi de Progression Stricte
* **Contenu** : Algorithme bloquant l'accès aux chapitres suivants et aux évaluations tant que la progression globale n'est pas de 100%.
* **Visuel** : **[Capture d'écran 4]** Visualisation des chapitres et de la jauge de progression (Vue Étudiant) + **[Capture d'écran 5]** Message de blocage d'accès ("Progression incomplète").

## 9. Sprint 3 : Modules d'Évaluation (Quiz)
* **Contenu** : Modèle de quiz (QCM). Pipeline transactionnel pour la soumission et le calcul algorithmique instantané des scores.
* **Visuel** : **[Capture d'écran 6]** Passage d'un quiz interactif (Vue Étudiant) et **[Capture d'écran 7]** Résultat avec score après soumission.

## 10. Sprint 3 (Suite) : Exercices Pratiques et Analytique
* **Contenu** : Gestion des livrables pour les exercices pratiques et suivi des performances des étudiants par les enseignants.
* **Visuel** : **[Capture d'écran 8]** Interface de soumission d'un exercice et/ou **[Capture d'écran 9]** Tableau de bord analytique Enseignant (Historique complet des quiz et succès des étudiants).

## 11. Sprint 4 : Moteur de Recommandation IA
* **Contenu** : Fonctionnement du modèle Sentence-BERT (NLP Bilingue). Pipeline de recommandation (extraction, expansion sémantique, similarité cosinus, scoring hybride).
* **Visuel** : Schéma du flux de recommandation (Requête -> NLP -> Sentence-BERT -> Résultats).

## 12. Sprint 4 (Suite) : Détection des Lacunes et Tuteur (Gemini)
* **Contenu** : Algorithme mappant les erreurs du quiz vers les chapitres spécifiques à réviser. Intégration de l'API Gemini pour générer des explications formatrices.
* **Visuel** : **[Capture d'écran 10]** Interface de l'étudiant montrant les points faibles détectés par l'IA ("Points à améliorer" / "Insights IA").

## 13. Sprint 5 : Messagerie et Notifications Intelligentes
* **Contenu** : Architecture d'envoi d'emails (SMTP). Notifications automatiques lors de la mise à jour des cours, relances pour inactivité, et sécurisation par mot de passe oublié.
* **Visuel** : **[Capture d'écran 11]** Exemple d'email reçu (Notification de nouveau chapitre) ou Interface de réinitialisation du mot de passe.

## 14. Bilan Technique et Technologique
* **Contenu** : Récapitulatif des technologies utilisées. Focus sur les points forts : Performance (FastAPI + Spring), Sécurité (JWT), Expérience utilisateur fluide (React).
* **Visuel** : Nuage de logos des technologies (React, Spring Boot, PostgreSQL, FastAPI, Python, JWT, Supabase).

## 15. Conclusion et Perspectives
* **Contenu** : Résumé des acquis (Plateforme complète, robuste et intelligente). Perspectives d'évolution : Gamification (système de badges), intégration de visioconférence (WebRTC), ou application mobile native.
* **Visuel** : Remerciements et ouverture aux questions ("Merci de votre attention. Place aux questions !").

---

### 💡 Conseils pour la présentation :
* **Démo en direct (Optionnel mais recommandé)** : Si vous avez le temps, vous pouvez remplacer les slides 6 à 13 par une rapide démo vidéo pré-enregistrée ou en direct de 3 minutes naviguant dans l'application.
* **Ne lisez pas les slides** : Gardez peu de texte, privilégiez les mots-clés et expliquez les flux à l'oral.
* **Transitions** : Assurez-vous que le passage de parole entre Amin, Wassim et Donia soit fluide et corresponde aux slides (ex: Donia présente le Frontend/UI, Amin la sécurité/backend, Wassim l'IA).
