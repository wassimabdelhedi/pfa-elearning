import os
import google.generativeai as genai
from pydantic import BaseModel
from typing import List, Optional
import json

class QuestionFeedbackRequest(BaseModel):
    question_text: str
    student_answer: str
    correct_answer: str

class TutorFeedbackResponse(BaseModel):
    feedback: str

class BatchQuestionFeedbackRequest(BaseModel):
    questions: List[QuestionFeedbackRequest]

class BatchTutorFeedbackResponse(BaseModel):
    feedbacks: List[dict] # List of {"question": "...", "feedback": "..."}

# Try to configure Gemini
api_key = os.getenv("GEMINI_API_KEY", "")
if api_key:
    genai.configure(api_key=api_key)

def generate_tutor_feedback(request: QuestionFeedbackRequest) -> TutorFeedbackResponse:
    if not api_key:
        return TutorFeedbackResponse(
            feedback=(
                "⚠️ **Clé d'API manquante** : Pour activer l'IA du tuteur pédagogique, "
                "veuillez ajouter `GEMINI_API_KEY=votre_cle` dans votre système ou fichier .env."
            )
        )
    
    prompt = f"""
Tu es un tuteur pédagogique numérique spécialisé dans l’enseignement universitaire.

Contexte :
Un étudiant a répondu incorrectement à une question de quiz.
Ton rôle est de l’aider à comprendre son erreur de manière claire, bienveillante et pédagogique,
sans jugement et sans donner immédiatement la réponse comme un simple corrigé.

Règles importantes :
- Sois TRÈS CONCIS et direct (maximum 3 phrases courtes).
- Explique rapidement pourquoi c'est incorrect et donne le bon concept.
- N'utilise pas de longs paragraphes ni d'introduction inutile.
- Le ton doit rester bienveillant et professionnel.
- Ne donnes pas directement la solution finale sous forme de réponse brute, guide vers la compréhension.

Données de la question :
Question : {request.question_text}
Réponse donnée par l’étudiant : {request.student_answer}
Réponse correcte : {request.correct_answer}

Structure attendue de ta réponse :
1. Une brève correction bienveillante.
2. L'explication courte du concept clé (1 ou 2 phrases maximum).
"""
    
    try:
        model = genai.GenerativeModel('gemini-flash-latest')
        response = model.generate_content(prompt)
        return TutorFeedbackResponse(feedback=response.text)
    except Exception as e:
        return TutorFeedbackResponse(feedback=f"⚠️ Erreur lors de la génération du feedback : {str(e)}")


def generate_batch_tutor_feedback(request: BatchQuestionFeedbackRequest) -> BatchTutorFeedbackResponse:
    if not api_key:
        return BatchTutorFeedbackResponse(feedbacks=[])
    
    if not request.questions:
        return BatchTutorFeedbackResponse(feedbacks=[])

    # Build a single prompt for all questions to be much faster
    prompt = """
Tu es un tuteur pédagogique numérique. Analyse les erreurs suivantes de l'étudiant.
Pour chaque question, fournis une explication TRÈS CONCISE (max 2 phrases).
Sois bienveillant et pédagogique.

Réponds UNIQUEMENT sous forme de liste JSON d'objets : [{"question": "...", "feedback": "..."}]
"""

    for i, q in enumerate(request.questions):
        prompt += f"\n--- Question {i+1} ---\n"
        prompt += f"Texte: {q.question_text}\n"
        prompt += f"Réponse étudiant: {q.student_answer}\n"
        prompt += f"Réponse correcte: {q.correct_answer}\n"

    try:
        model = genai.GenerativeModel('gemini-flash-latest')
        response = model.generate_content(prompt)
        text = response.text.strip()
        
        # Clean potential markdown code blocks
        if text.startswith("```json"):
            text = text[7:]
        if text.endswith("```"):
            text = text[:-3]
        
        feedbacks = json.loads(text)
        return BatchTutorFeedbackResponse(feedbacks=feedbacks)
    except Exception as e:
        print(f"Batch tutor error: {e}")
        # Fallback: empty list
        return BatchTutorFeedbackResponse(feedbacks=[])

