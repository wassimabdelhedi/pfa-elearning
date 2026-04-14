"""
Routes API pour le moteur de recommandation
Endpoints appelés par Spring Boot
"""

import logging
from typing import List
from fastapi import APIRouter, HTTPException, UploadFile, File

from app.schemas.recommendation_schema import (
    RecommendRequest,
    RecommendResponse,
    RecommendationItem,
    IndexCourseRequest,
    IndexCourseResponse,
    WeakTopicQuestion,
    DetectWeakTopicsRequest,
    WeakTopicResponse,
)
from app.models.recommender import CourseRecommender
from app.models.nlp_processor import NLPProcessor
from app.services.text_extractor import extract_text
from app.services.weak_topic_detector import detect_weak_topics


logger = logging.getLogger(__name__)

router = APIRouter()

# Initialiser le recommender au démarrage (singleton)
recommender = CourseRecommender()
nlp = NLPProcessor()


@router.post("/recommend", response_model=RecommendResponse)
async def recommend_courses(request: RecommendRequest):
    """
    Endpoint principal de recommandation.
    Reçoit la requête de l'étudiant + tous les cours publiés,
    retourne les cours les plus pertinents triés par score.
    
    Appelé par Spring Boot: POST http://localhost:8000/api/recommend
    """
    try:
        logger.info(f"Recommendation request: query='{request.query}', "
                     f"student_id={request.student_id}, "
                     f"courses_count={len(request.courses)}")

        # Convertir les cours Pydantic en dicts
        courses_data = [course.model_dump() for course in request.courses]

        # Extraire les mots-clés de la requête
        keywords = nlp.extract_keywords(request.query)

        # Obtenir les recommandations
        results = recommender.recommend(
            query=request.query,
            courses=courses_data,
            enrolled_course_ids=request.enrolled_courses,
            top_n=10
        )

        # Construire la réponse
        recommendations = [
            RecommendationItem(
                course_id=r["course_id"],
                score=r["score"],
                reason=r["reason"]
            )
            for r in results
        ]

        return RecommendResponse(
            recommendations=recommendations,
            extracted_keywords=keywords,
            query=request.query
        )

    except Exception as e:
        logger.error(f"Recommendation error: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Recommendation error: {str(e)}")


@router.post("/index-course", response_model=IndexCourseResponse)
async def index_course(request: IndexCourseRequest):
    """
    Indexe un nouveau cours dans le moteur de recommandation.
    Appelé par Spring Boot quand un enseignant publie ou modifie un cours.
    
    Appelé par Spring Boot: POST http://localhost:8000/api/index-course
    """
    try:
        logger.info(f"Indexing course: id={request.id}, title='{request.title}'")

        course_data = request.model_dump()
        keywords = recommender.index_course(course_data)

        return IndexCourseResponse(
            status="indexed",
            course_id=request.id,
            keywords=keywords
        )

    except Exception as e:
        logger.error(f"Indexing error: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Indexing error: {str(e)}")


@router.post("/extract-keywords")
async def extract_keywords(body: dict):
    """
    Extrait les mots-clés d'un texte donné.
    Utile pour le debug et les tests.
    """
    text = body.get("text", "")
    keywords = nlp.extract_keywords(text)
    language = nlp.detect_language(text)

    return {
        "text": text,
        "keywords": keywords,
        "language": language,
        "count": len(keywords)
    }


@router.post("/extract-text")
async def extract_text_from_file(file: UploadFile = File(...)):
    """
    Extrait le texte d'un fichier uploadé (PDF, DOCX, PPTX, TXT).
    Appelé par Spring Boot lors de la création/modification d'un cours.
    
    Appelé par Spring Boot: POST http://localhost:8000/api/extract-text
    """
    try:
        # Vérifier le type de fichier
        allowed_extensions = [".pdf", ".docx", ".pptx", ".txt"]
        filename = file.filename or "unknown"
        file_ext = "." + filename.rsplit(".", 1)[-1].lower() if "." in filename else ""

        if file_ext not in allowed_extensions:
            raise HTTPException(
                status_code=400,
                detail=f"Type de fichier non supporté: {file_ext}. Types acceptés: {', '.join(allowed_extensions)}"
            )

        logger.info(f"Extracting text from: {filename}")

        # Lire le contenu du fichier
        file_bytes = await file.read()

        # Extraire le texte
        result = extract_text(file_bytes, filename)

        logger.info(f"Extracted {result['char_count']} chars from {filename}")

        return result

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Text extraction error: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Erreur d'extraction: {str(e)}")




@router.post("/detect-weak-topics", response_model=WeakTopicResponse)
async def analyze_quiz_failure(request: DetectWeakTopicsRequest):
    """
    Analyse les questions d'un quiz pour identifier les lacunes.
    Si les topics sont absents des questions, utilise les chapitres pour mapper.
    """
    try:
        # Convertir les objets Pydantic en dictionnaires
        questions_data = [q.model_dump() for q in request.questions]
        chapters_data = [c.model_dump() for c in request.chapters] if request.chapters else None
        
        result = detect_weak_topics(questions_data, chapters_data, recommender)
        return result
        
    except Exception as e:
        logger.error(f"Weak topic detection error: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail=f"Erreur d'analyse: {str(e)}")
