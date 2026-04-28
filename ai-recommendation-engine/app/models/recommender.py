"""
Course Recommender - Moteur de recommandation basé sur Sentence-BERT
Utilise le modèle multilingue pour encoder les requêtes et les cours,
puis calcule la similarité cosinus pour trouver les cours les plus pertinents.
"""

import logging
from typing import List, Dict, Any, Optional
import numpy as np
from sentence_transformers import SentenceTransformer
from sklearn.metrics.pairwise import cosine_similarity

from app.models.nlp_processor import NLPProcessor

logger = logging.getLogger(__name__)


class CourseRecommender:
    """
    Moteur de recommandation utilisant Sentence-BERT multilingue.
    
    Le modèle 'paraphrase-multilingual-MiniLM-L12-v2' supporte 50+ langues
    dont le français et l'anglais, ce qui permet de faire des recherches
    cross-lingues (recherche en français → résultats en anglais et vice versa).
    """

    def __init__(self, model_name: str = "paraphrase-multilingual-MiniLM-L12-v2"):
        logger.info(f"Loading Sentence-BERT model: {model_name}")
        self.model = SentenceTransformer(model_name)
        self.nlp = NLPProcessor()
        self.course_cache: Dict[int, Dict[str, Any]] = {}
        self.course_embeddings: Optional[np.ndarray] = None
        self.course_ids: List[int] = []
        logger.info("Model loaded successfully!")

    def _build_course_text(self, course: Dict[str, Any]) -> str:
        """Construit le texte représentatif d'un cours pour l'embedding"""
        return self.nlp.build_search_text(
            title=course.get("title", ""),
            description=course.get("description", ""),
            content=course.get("content", ""),
            category=course.get("category", "")
        )

    def recommend(
        self,
        query: str,
        courses: List[Dict[str, Any]],
        enrolled_course_ids: List[int] = None,
        top_n: int = 10
    ) -> List[Dict[str, Any]]:
        """
        Recommande les cours les plus pertinents pour une requête donnée.
        
        Pipeline:
        1. Encode la requête avec Sentence-BERT
        2. Encode tous les cours (ou utilise le cache)
        3. Calcule la similarité cosinus
        4. Ajuste les scores selon le profil étudiant
        5. Retourne les top N résultats
        
        Args:
            query: La recherche de l'étudiant (FR ou EN)
            courses: Liste des cours avec title, description, content
            enrolled_course_ids: IDs des cours déjà suivis par l'étudiant
            top_n: Nombre de résultats à retourner
            
        Returns:
            Liste de {course_id, score, reason}
        """
        if not courses:
            return []

        if enrolled_course_ids is None:
            enrolled_course_ids = []

        # 1. Extraire les mots-clés de la requête
        keywords = self.nlp.extract_keywords(query)
        language = self.nlp.detect_language(query)
        
        # Expansion de la requête pour enrichir l'embedding sémantique
        # Si l'utilisateur tape "ia", on veut aussi que le modèle cherche "intelligence artificielle"
        expanded_query = query
        for kw in keywords:
            if kw in self.nlp.tech_synonyms:
                syns = [s for s in self.nlp.tech_synonyms[kw] if s.lower() not in query.lower()]
                if syns:
                    expanded_query += " " + " ".join(syns)
        
        logger.info(f"Query: '{query}' | Expanded: '{expanded_query}' | Language: {language} | Keywords: {keywords}")

        # 2. Encoder la requête (version étendue)
        query_embedding = self.model.encode([expanded_query])

        # 3. Encoder tous les cours
        course_texts = [self._build_course_text(c) for c in courses]
        course_embeddings = self.model.encode(course_texts)

        # 4. Calculer la similarité cosinus (base sémantique)
        similarities = cosine_similarity(query_embedding, course_embeddings).flatten()

        # 5. Raffinement de la précision (Scoring Hybride)
        adjusted_scores = np.copy(similarities)
        
        # Détection de l'intention de niveau (débutant, expert, etc.)
        query_lower = query.lower()
        target_level = None
        if any(w in query_lower for w in ["débutant", "beginner", "introduction", "bases", "fondamentaux"]):
            target_level = "BEGINNER"
        elif any(w in query_lower for w in ["intermédiaire", "intermediate", "moyen"]):
            target_level = "INTERMEDIATE"
        elif any(w in query_lower for w in ["expert", "avancé", "advanced", "maîtrise"]):
            target_level = "EXPERT"

        for i, course in enumerate(courses):
            course_id = course.get("id")
            course_title = course.get("title", "").lower()
            course_desc = course.get("description", "").lower()
            course_cat = course.get("category", "").lower()
            course_lvl = course.get("level", "BEGINNER")

            # A. Pénaliser les cours déjà inscrits (pertinence de découverte)
            if course_id in enrolled_course_ids:
                adjusted_scores[i] *= 0.2  # Pénalité plus forte pour favoriser la découverte

            # B. Bonus pour correspondance EXACTE dans le titre (très haute précision)
            # Si le mot exact cherché est dans le titre, c'est souvent ce que l'utilisateur veut
            for kw in keywords:
                if f" {kw} " in f" {course_title} ":
                    adjusted_scores[i] += 0.15 # Bonus additif pour garantir une remontée
                elif kw in course_title:
                    adjusted_scores[i] += 0.05

            # C. Bonus pour la catégorie
            if any(kw in course_cat for kw in keywords):
                adjusted_scores[i] += 0.1

            # D. Match de niveau (Précision contextuelle)
            if target_level and course_lvl == target_level:
                adjusted_scores[i] += 0.1  # Bonus si le niveau correspond à l'intention
            elif target_level and course_lvl != target_level:
                adjusted_scores[i] -= 0.05 # Légère pénalité si le niveau ne correspond pas

            # E. Bonus pour les cours "complets" (description longue = souvent plus de contenu)
            if len(course_desc) > 200:
                adjusted_scores[i] += 0.02

        # 6. Trier par score décroissant
        top_indices = np.argsort(adjusted_scores)[::-1][:top_n]

        # 7. Construire les résultats
        results = []
        for idx in top_indices:
            score = float(adjusted_scores[idx])
            # Seuil de pertinence : on veut de la précision, donc on ignore les scores trop bas
            if score < 0.1: 
                continue

            course = courses[idx]
            reason = self._generate_reason(query, course, keywords, score, language)

            results.append({
                "course_id": course["id"],
                "score": min(1.0, round(score, 4)), # Cap à 1.0
                "reason": reason
            })

        logger.info(f"Returning {len(results)} recommendations with improved precision")
        return results

    def _generate_reason(
        self,
        query: str,
        course: Dict[str, Any],
        keywords: List[str],
        score: float,
        language: str
    ) -> str:
        """Génère une explication lisible pour la recommandation"""
        title = course.get("title", "")
        category = course.get("category", "")

        # Trouver les mots-clés qui matchent
        matching_keywords = [
            kw for kw in keywords
            if kw in title.lower() or kw in course.get("description", "").lower()
        ]

        if language == "fr":
            if matching_keywords:
                return f"Correspond à votre recherche sur : {', '.join(matching_keywords[:3])}"
            elif category:
                return f"Cours pertinent dans la catégorie : {category}"
            else:
                return f"Pertinent pour votre recherche (score: {score:.0%})"
        else:
            if matching_keywords:
                return f"Matches your search for: {', '.join(matching_keywords[:3])}"
            elif category:
                return f"Relevant course in category: {category}"
            else:
                return f"Relevant to your search (score: {score:.0%})"

    def extract_course_keywords(self, title: str, description: str, content: str) -> str:
        """Extrait les mots-clés d'un cours pour stockage en DB"""
        full_text = f"{title} {description} {content}"
        keywords = self.nlp.extract_keywords(full_text)
        return ", ".join(keywords[:15])

    def index_course(self, course_data: Dict[str, Any]) -> str:
        """Indexe un cours dans le cache pour des recherches plus rapides"""
        course_id = course_data.get("id")
        self.course_cache[course_id] = course_data

        # Extraire les mots-clés
        keywords = self.extract_course_keywords(
            course_data.get("title", ""),
            course_data.get("description", ""),
            course_data.get("content", "")
        )

        logger.info(f"Indexed course {course_id} with keywords: {keywords}")
        return keywords
