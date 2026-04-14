from typing import List, Dict, Any, Optional
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity

def detect_weak_topics(
    questions: List[Dict[str, Any]], 
    chapters: List[Dict[str, Any]] = None,
    recommender: Any = None
) -> Dict[str, Any]:
    """
    Analyse les réponses aux questions d'un quiz pour détecter les concepts non maîtrisés.
    Si les topics sont absents, ils sont déduits par similarité sémantique avec les chapitres du cours.
    """
    if not questions:
        return {
            "failed": False,
            "weak_topics": [],
            "global_summary": "Aucune question fournie pour l'analyse."
        }

    # 1. Préparer les embeddings des chapitres si nécessaire
    chapter_embeddings = None
    if chapters and recommender:
        chapter_texts = [f"{c.get('title', '')} {c.get('content', '')}" for c in chapters]
        chapter_embeddings = recommender.model.encode(chapter_texts)

    total_questions = len(questions)
    correct_answers = 0
    topic_stats = {}

    # 2. Analyser les questions
    for q in questions:
        student_ans = q.get("student_answer")
        correct_ans = q.get("correct_answer")
        is_correct = (student_ans == correct_ans)
        
        if is_correct:
            correct_answers += 1
            
        # Déterminer le topic
        topic = q.get("topic")
        
        # Si le topic est absent ou générique, on utilise la similarité sémantique avec les chapitres
        if (not topic or topic.lower() in ["unknown", "general", "general topics"]) and chapter_embeddings is not None:
            question_text = q.get("text", "")
            q_embedding = recommender.model.encode([question_text])
            
            # Calculer la similarité avec chaque chapitre
            similarities = cosine_similarity(q_embedding, chapter_embeddings).flatten()
            best_idx = np.argmax(similarities)
            
            if similarities[best_idx] > 0.3: # Seuil minimum de confiance
                topic = chapters[best_idx].get("title", "Unknown Topic")
            else:
                topic = "Concepts Généraux"
        
        if not topic:
            topic = "Général"

        # Statistiques par topic
        if topic not in topic_stats:
            topic_stats[topic] = {"total": 0, "incorrect": 0}
            
        topic_stats[topic]["total"] += 1
        if not is_correct:
            topic_stats[topic]["incorrect"] += 1

    # Calcul du score global
    score_percentage = (correct_answers / total_questions) * 100

    # Si l'étudiant a réussi (>= 60%), on ne signale pas de lacunes majeures
    if score_percentage >= 60:
        return {
            "failed": False,
            "weak_topics": [],
            "global_summary": f"Réussite avec {score_percentage:.1f}%. Aucune lacune critique détectée."
        }

    weak_topics = []
    critical_topics = []

    # 3. Évaluer la sévérité par topic
    for topic, stats in topic_stats.items():
        if stats["incorrect"] > 0:
            error_rate = (stats["incorrect"] / stats["total"]) * 100
            
            if error_rate > 70:
                severity = "critical"
                critical_topics.append(topic)
            elif error_rate >= 40:
                severity = "medium"
            else:
                severity = "low"
                
            weak_topics.append({
                "topic": topic,
                "severity": severity
            })

    # Génération du résumé
    if critical_topics:
        topics_str = ", ".join(critical_topics)
        summary = f"Lacunes critiques identifiées sur : {topics_str}. Révision recommandée."
    elif weak_topics:
        summary = "Certains concepts nécessitent une révision pour valider le module."
    else:
        summary = "Score insuffisant, bien que les concepts individuels soient partiellement compris."

    return {
        "failed": True,
        "weak_topics": weak_topics,
        "global_summary": summary
    }
