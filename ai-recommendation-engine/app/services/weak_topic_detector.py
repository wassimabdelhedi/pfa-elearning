from typing import List, Dict, Any

def detect_weak_topics(questions: List[Dict[str, Any]]) -> Dict[str, Any]:
    """
    Analyse les réponses aux questions d'un quiz pour détecter les concepts non maîtrisés.
    
    Args:
        questions (List[Dict[str, Any]]): Liste des questions avec texte, topic, réponse étudiant et réponse correcte.
        
    Returns:
        Dict[str, Any]: Rapport contenant l'état de réussite, la liste des topics faibles, et un résumé global.
    """
    if not questions:
        return {
            "failed": False,
            "weak_topics": [],
            "global_summary": "Aucune question fournie pour l'analyse."
        }

    total_questions = len(questions)
    correct_answers = 0
    topic_stats = {}

    # 1 & 2. Analyser et regrouper par topic
    for q in questions:
        topic = q.get("topic", "Unknown")
        student_ans = q.get("student_answer")
        correct_ans = q.get("correct_answer")
        
        # Vérifier si la réponse est correcte
        is_correct = (student_ans == correct_ans)
        if is_correct:
            correct_answers += 1
            
        # Initialiser les statistiques pour ce topic
        if topic not in topic_stats:
            topic_stats[topic] = {"total": 0, "incorrect": 0}
            
        topic_stats[topic]["total"] += 1
        
        # Uniquement comptabiliser les réponses incorrectes
        if not is_correct:
            topic_stats[topic]["incorrect"] += 1

    # Calcul du score global
    score_percentage = (correct_answers / total_questions) * 100

    # Retour rapide si score global >= 60%
    if score_percentage >= 60:
        return {
            "failed": False,
            "weak_topics": [],
            "global_summary": f"Félicitations, l'étudiant a réussi le quiz avec un score de {score_percentage:.1f}%."
        }

    weak_topics = []
    critical_topics = []

    # 3 & 4. Calculer le % d'erreur et attribuer la sévérité
    for topic, stats in topic_stats.items():
        if stats["incorrect"] > 0:
            # Pourcentage d'erreur pour CE topic
            error_rate = (stats["incorrect"] / stats["total"]) * 100
            
            if error_rate > 70:
                severity = "critical"
                critical_topics.append(topic)
            elif 40 <= error_rate <= 70:
                severity = "medium"
            else:
                severity = "low"
                
            weak_topics.append({
                "topic": topic,
                "severity": severity
            })

    # Génération du résumé en fonction des résultats
    if critical_topics:
        topics_str = ", ".join(critical_topics)
        summary = f"L'étudiant présente des lacunes importantes sur le(s) sujet(s) : {topics_str}."
    elif weak_topics:
        summary = "Le quiz n'est pas validé. Quelques erreurs à revoir sur certains concepts."
    else:
        summary = "Le score global est insuffisant (<60%), mais les erreurs sont isolées par sujet."

    return {
        "failed": True,
        "weak_topics": weak_topics,
        "global_summary": summary
    }
