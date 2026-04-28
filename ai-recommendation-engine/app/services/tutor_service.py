import os
import google.generativeai as genai
from pydantic import BaseModel
from typing import List, Optional
import json

class QuestionFeedbackRequest(BaseModel):
    question_id: Optional[int] = None
    question_text: str
    student_answer: str
    correct_answer: str

class TutorFeedbackResponse(BaseModel):
    feedback: str

class BatchQuestionFeedbackRequest(BaseModel):
    questions: List[QuestionFeedbackRequest]

class BatchTutorFeedbackResponse(BaseModel):
    feedbacks: List[dict] # List of {"question_id": 123, "feedback": "..."}

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

    # HARDCODED TEST to verify the pipe works
    # return BatchTutorFeedbackResponse(feedbacks=[{"question_id": q.question_id, "feedback": "Test feedback for " + q.question_text} for q in request.questions])

    prompt = """
Tu es un tuteur pédagogique numérique. Analyse les erreurs suivantes de l'étudiant.
Pour chaque question, fournis une explication TRÈS CONCISE (max 2 sentences).
Sois bienveillant et pédagogique.

Tu DOIS retourner le résultat UNIQUEMENT sous forme de liste JSON d'objets : [{"question_id": ID, "feedback": "EXPLICATION"}]
"""

    for i, q in enumerate(request.questions):
        prompt += f"\n--- Question {i+1} (ID: {q.question_id}) ---\n"
        prompt += f"Texte: {q.question_text}\n"
        prompt += f"Réponse étudiant: {q.student_answer}\n"
        prompt += f"Réponse correcte: {q.correct_answer}\n"

    # Chaîne de secours étendue pour garantir ZERO interruption
    models_to_try = [
        'gemini-2.0-flash', 
        'gemini-2.0-flash-lite', 
        'gemini-2.5-flash', 
        'gemini-pro-latest',
        'gemini-2.0-flash-001'
    ]
    
    last_error = ""
    for model_name in models_to_try:
        try:
            model = genai.GenerativeModel(model_name)
            response = model.generate_content(prompt)
            text = response.text.strip()
            
            # Nettoyage JSON
            if "```json" in text:
                text = text.split("```json")[1].split("```")[0].strip()
            elif "```" in text:
                text = text.split("```")[1].split("```")[0].strip()
            
            start = text.find('[')
            end = text.rfind(']') + 1
            if start != -1 and end != 0:
                text = text[start:end]
                
            feedbacks = json.loads(text)
            return BatchTutorFeedbackResponse(feedbacks=feedbacks)
            
        except Exception as e:
            last_error = str(e)
            if "429" in last_error:
                continue # Essayer le modèle suivant si quota dépassé
            break # Arrêter si c'est une autre erreur (ex: clé invalide)

    # Si tous les modèles échouent
    return BatchTutorFeedbackResponse(feedbacks=[
        {"question_id": q.question_id, "feedback": f"Analyse temporairement indisponible (Quota atteint). Veuillez réessayer dans 1 minute."} 
        for q in request.questions
    ])

