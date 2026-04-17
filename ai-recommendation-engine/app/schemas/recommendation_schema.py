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


class WeakTopicQuestion(BaseModel):
    text: str
    topic: Optional[str] = None
    student_answer: str
    correct_answer: str


class ChapterData(BaseModel):
    id: int
    title: str
    content: str = ""


class DetectWeakTopicsRequest(BaseModel):
    questions: List[WeakTopicQuestion]
    chapters: List[ChapterData] = []


class WeakTopicItem(BaseModel):
    topic: str
    severity: str


class WeakTopicResponse(BaseModel):
    failed: bool
    weak_topics: List[WeakTopicItem]
    global_summary: str


class QuestionFeedbackRequest(BaseModel):
    question_text: str
    student_answer: str
    correct_answer: str


class TutorFeedbackResponse(BaseModel):
    feedback: str
