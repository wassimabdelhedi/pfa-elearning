"""
Pydantic schemas pour les requêtes/réponses de l'API IA
"""

from pydantic import BaseModel
from typing import List, Optional


class CourseData(BaseModel):
    """Données d'un cours envoyées par Spring Boot"""
    id: int
    title: str
    description: str = ""
    content: str = ""
    level: str = "BEGINNER"
    category: str = ""


class RecommendRequest(BaseModel):
    """Requête de recommandation depuis Spring Boot"""
    query: str
    student_id: int
    enrolled_courses: List[int] = []
    courses: List[CourseData]


class RecommendationItem(BaseModel):
    """Un cours recommandé avec son score"""
    course_id: int
    score: float
    reason: str


class RecommendResponse(BaseModel):
    """Réponse de recommandation"""
    recommendations: List[RecommendationItem]
    extracted_keywords: List[str]
    query: str


class IndexCourseRequest(BaseModel):
    """Requête pour indexer un nouveau cours"""
    id: int
    title: str
    description: str = ""
    content: str = ""
    category: str = ""


class IndexCourseResponse(BaseModel):
    """Réponse après indexation d'un cours"""
    status: str
    course_id: int
    keywords: str
