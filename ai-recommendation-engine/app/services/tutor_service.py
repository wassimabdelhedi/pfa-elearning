import os
import logging
from google import genai
from app.schemas.recommendation_schema import QuestionFeedbackRequest, TutorFeedbackResponse

logger = logging.getLogger(__name__)

# Configure Gemini Client
api_key = os.getenv("GEMINI_API_KEY")
if api_key:
    client = genai.Client(api_key=api_key)
    # Test avec un modèle Gemma qui a souvent des quotas différents
    MODEL_ID = "gemma-3-27b-it"
else:
    logger.warning("GEMINI_API_KEY not found in environment variables")
    client = None

def generate_tutor_feedback(request: QuestionFeedbackRequest) -> TutorFeedbackResponse:
    """
    Génère un feedback pédagogique utilisant Gemini 2.0 Flash.
    """
    if not client:
        return TutorFeedbackResponse(feedback="Le service de tutorat IA est actuellement indisponible (Clé API manquante).")

    prompt = f"""
    Tu es un tuteur pédagogique bienveillant et expert. 
    Un étudiant a répondu à une question de quiz mais s'est trompé.
    
    Question: {request.question_text}
    Réponse de l'étudiant: {request.student_answer}
    Bonne réponse: {request.correct_answer}
    
    Instructions:
    1. Ne donne pas directement la réponse si elle n'est pas déjà évidente, mais explique le CONCEPT derrière l'erreur.
    2. Sois encourageant et empathique.
    3. Utilise le tutoiement si approprié ou un ton professionnel mais chaleureux.
    4. Réponds en FRANÇAIS.
    5. Ta réponse doit être concise (max 3-4 phrases).
    
    Format de sortie: Juste le texte du feedback, sans fioritures.
    """

    try:
        response = client.models.generate_content(
            model=MODEL_ID,
            contents=prompt
        )
        feedback_text = response.text.strip()
        return TutorFeedbackResponse(feedback=feedback_text)
    except Exception as e:
        logger.error(f"Error generating Gemini feedback: {str(e)}")
        return TutorFeedbackResponse(feedback="Désolé, je n'ai pas pu générer de feedback pour le moment. Révise bien ton cours !")


async def generate_batch_tutor_feedback(requests: list) -> list:
    """
    Exécute plusieurs générations de feedback en parallèle.
    """
    import asyncio
    
    # On utilise asyncio.to_thread pour ne pas bloquer l'évent loop avec les appels synchrones du SDK
    tasks = [asyncio.to_thread(generate_tutor_feedback, req) for req in requests]
    
    # Lancement en parallèle
    responses = await asyncio.gather(*tasks)
    
    # Formatage pour correspondre à ce que le backend Java attend
    return [
        {"question": req.question_text, "feedback": res.feedback} 
        for req, res in zip(requests, responses)
    ]
