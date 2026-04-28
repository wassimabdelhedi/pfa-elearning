"""
NLP Processor - Extraction de mots-clés bilingue (FR/EN)
Utilise des techniques de base pour l'extraction de mots-clés
sans dépendance lourde à spaCy models au démarrage
"""

import re
from typing import List, Set

# Stop words français
FRENCH_STOP_WORDS: Set[str] = {
    "le", "la", "les", "de", "du", "des", "un", "une", "et", "est", "en",
    "que", "qui", "dans", "pour", "pas", "sur", "ce", "il", "elle", "sont",
    "au", "aux", "avec", "son", "sa", "ses", "ou", "mais", "donc", "car",
    "ne", "se", "si", "leur", "leurs", "nous", "vous", "ils", "elles",
    "je", "tu", "me", "te", "mon", "ma", "mes", "ton", "ta", "tes",
    "notre", "votre", "nos", "vos", "été", "être", "avoir", "fait",
    "comme", "par", "plus", "tout", "très", "aussi", "bien", "peut",
    "même", "entre", "après", "avant", "cette", "ces", "autres",
    "quand", "comment", "où", "quel", "quelle", "quels", "quelles",
    "faire", "dit", "peu", "sous", "vers", "chez", "sans", "encore",
    "veux", "vouloir", "voudrais", "apprendre", "comprendre", "savoir",
    "cours", "cherche", "besoin", "aide", "trouver", "avoir"
}

# Stop words anglais
ENGLISH_STOP_WORDS: Set[str] = {
    "the", "a", "an", "is", "are", "was", "were", "be", "been", "being",
    "have", "has", "had", "do", "does", "did", "will", "would", "could",
    "should", "may", "might", "shall", "can", "need", "dare", "ought",
    "used", "to", "of", "in", "for", "on", "with", "at", "by", "from",
    "as", "into", "through", "during", "before", "after", "above",
    "below", "between", "out", "off", "over", "under", "again",
    "further", "then", "once", "here", "there", "when", "where",
    "why", "how", "all", "each", "every", "both", "few", "more",
    "most", "other", "some", "such", "no", "nor", "not", "only",
    "own", "same", "so", "than", "too", "very", "just", "because",
    "but", "and", "or", "if", "while", "about", "up", "it", "its",
    "i", "me", "my", "we", "our", "you", "your", "he", "him", "his",
    "she", "her", "they", "them", "their", "what", "which", "who",
    "this", "that", "these", "those", "am", "want", "learn", "know",
    "find", "help", "get", "make", "take", "give", "look", "come"
}

ALL_STOP_WORDS = FRENCH_STOP_WORDS | ENGLISH_STOP_WORDS


class NLPProcessor:
    """
    Processeur NLP pour l'extraction de mots-clés bilingue.
    Supporte le français et l'anglais.
    """

    def __init__(self):
        # Dictionnaire de synonymes/termes techniques pour enrichir les recherches
        self.tech_synonyms = {
            "ia": ["intelligence artificielle", "artificial intelligence", "ai", "machine learning"],
            "ai": ["artificial intelligence", "intelligence artificielle", "ia", "machine learning"],
            "ml": ["machine learning", "apprentissage automatique", "apprentissage machine"],
            "dl": ["deep learning", "apprentissage profond"],
            "poo": ["programmation orientée objet", "object oriented programming", "oop"],
            "bdd": ["base de données", "database", "sql"],
            "js": ["javascript", "web"],
            "ts": ["typescript"],
            "algo": ["algorithme", "algorithm", "algorithmique"],
            "dev": ["développement", "development", "programming", "programmation"],
            "web": ["développement web", "web development"],
            "api": ["interface de programmation", "application programming interface"],
            "ui": ["interface utilisateur", "user interface"],
            "ux": ["expérience utilisateur", "user experience"],
            "react": ["frontend", "javascript", "framework", "web"],
            "java": ["backend", "spring", "spring boot", "poo"],
            "python": ["data science", "ia", "scripting", "backend"],
            "data": ["données", "big data", "analyse"],
            "docker": ["conteneur", "container", "devops"],
            "git": ["versioning", "github", "gitlab"],
            "cloud": ["aws", "azure", "infrastructure"],
            "sécurité": ["cybersecurity", "security", "hacking", "protection"],
        }

    def extract_keywords(self, text: str) -> List[str]:
        """
        Extrait les mots-clés significatifs d'un texte.
        Fonctionne en FR et EN.
        """
        # Nettoyage (on garde + et # pour C++ et C#)
        text = text.lower().strip()
        text = re.sub(r'[^\w\s\-\+\#]', ' ', text)

        # Tokenization
        words = text.split()

        # Filtrage des stop words et mots courts (mais on garde les termes techniques de 2 lettres comme IA, AI, JS)
        base_keywords = [
            word for word in words
            if word not in ALL_STOP_WORDS
            and (len(word) > 2 or word in self.tech_synonyms)
            and not word.isdigit()
        ]

        # Enrichir avec les synonymes techniques
        enriched = list(base_keywords)
        for word in base_keywords:
            if word in self.tech_synonyms:
                for syn in self.tech_synonyms[word]:
                    if syn not in enriched:
                        enriched.append(syn)

        # Déduplication tout en préservant l'ordre
        seen = set()
        unique_keywords = []
        for kw in enriched:
            if kw not in seen:
                seen.add(kw)
                unique_keywords.append(kw)

        return unique_keywords[:20]  # Max 20 mots-clés

    def detect_language(self, text: str) -> str:
        """Détecte si le texte est en français ou en anglais (heuristique simple)"""
        french_indicators = {"le", "la", "les", "des", "une", "est", "sont", "dans", "pour", "avec", "sur"}
        english_indicators = {"the", "is", "are", "for", "with", "and", "this", "that", "from", "have"}

        words = set(text.lower().split())
        fr_score = len(words & french_indicators)
        en_score = len(words & english_indicators)

        return "fr" if fr_score > en_score else "en"

    def build_search_text(self, title: str, description: str, content: str, category: str) -> str:
        """Construit le texte complet d'un cours pour la vectorisation"""
        parts = []
        if title:
            parts.append(title)
            parts.append(title)  # Double weight for title
            # Extraire les mots-clés du titre pour renforcer l'embedding
            title_kws = self.extract_keywords(title)
            parts.extend(title_kws)
            
        if category:
            parts.append(category)
            
        if description:
            # Nettoyer un peu la description pour l'embedding
            desc_clean = re.sub(r'<[^>]+>', '', description) # Enlever HTML si présent
            parts.append(desc_clean[:500])
            
        if content:
            # Extraire quelques mots-clés du contenu au lieu de tout mettre
            content_kws = self.extract_keywords(content[:5000])
            parts.extend(content_kws[:15])

        return " ".join(parts)
