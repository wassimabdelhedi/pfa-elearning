# AI Recommendation Engine Architecture (LearnAgent)

This document provides a detailed, comprehensive explanation of the Python AI Recommendation Engine (`ai-recommendation-engine`), which handles semantic search, document processing, and recommendation logic for the LearnAgent e-learning platform.

## 0. Visual Architecture & Directory Structure

```text
ai-recommendation-engine/
├── main.py                           # Entry point for the FastAPI server
├── requirements.txt                  # Python dependencies
└── app/
    ├── __init__.py
    ├── models/
    │   ├── nlp_processor.py          # Keyword extraction & Language detection
    │   └── recommender.py            # Sentence-BERT embeddings & Cosine Similarity
    ├── routes/
    │   └── recommendation_routes.py  # REST API Endpoints defined here
    ├── schemas/
    │   └── recommendation_schema.py  # Pydantic validation schemas
    └── services/
        ├── text_extractor.py         # Extracts text from incoming PDFs, PPTX, DOCX, TXT
        └── weak_topic_detector.py    # Analyzes quiz answers to find student weaknesses
```

## 1. Core Responsibilities

The AI engine is a standalone microservice responsible for compute-heavy Machine Learning and Natural Language Processing tasks. These tasks are cleanly separated from the Spring Boot backend to ensure performance and scalability:

- **Course Recommendation System**: Sorting through available courses to suggest the best matches for a student based on their search queries.
- **Document Text Extraction**: Reading raw uploaded files (PDF, PPTX, DOCX) and extracting readable text so that the Java backend can store it and the AI can index it.
- **Weak Topic Detection**: Analyzing a student's incorrect quiz answers and mapping them back to specific course chapters via semantic similarity. This highlights exact areas where the student requires further review.
- **Keyword Extraction**: Using NLTK and Spacy patterns to automatically summarize large blocks of course text into tagging keywords.

---

## 2. API Endpoints (`app/routes/recommendation_routes.py`)

The engine exposes a FastAPI REST application running on `http://localhost:8000`. The Spring Boot backend makes asynchronous requests to these endpoints:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/recommend` | `POST` | Takes a student's search query, their currently enrolled courses, and the full catalog. Returns top 10 recommended courses ranked by semantic similarity. |
| `/api/index-course` | `POST` | Processes and caches the vector embedding of a newly created course to speed up future recommendations. |
| `/api/extract-text` | `POST` | Accepts an `UploadFile` (multipart) and extracts plain text from it. |
| `/api/extract-keywords` | `POST` | Extracts NLP keywords from a plain text body. |
| `/api/detect-weak-topics`| `POST` | Receives quiz questions and student answers, compares them to chapter contents, and returns a summary list of weak concepts the student must review. |

---

## 3. The Recommendation Pipeline (`app/models/recommender.py`)

The Machine Learning pipeline is powered by the **`paraphrase-multilingual-MiniLM-L12-v2`** model from Sentence-Transformers. This allows it to work out-of-the-box with multiple languages (including French and English).

### Step-by-Step Flow:
1. **Embedding**: When a user searches for something (e.g., "Python pour débutants"), the text query is passed into the Transformer model, which outputs a high-dimensional vector (384 dimensions) representing the meaning of the sentence.
2. **Comparison**: The queried vector is mathematically compared against the pre-calculated vectors of all available courses using **Cosine Similarity** via `scikit-learn`'s metrics.
3. **Weighting**: The similarity score is dynamically adjusted based on business logic:
   - Matches targeting the course title directly receive a score bonus.
   - Courses the user is already enrolled in are heavily penalized so the engine focuses on suggesting *new* material.
   - Metadata matches (like matching the `Category`) add minor score bonuses.
4. **Ranking**: The results are sorted. The top `N` results are formatted with a generated `reason` string (e.g. "Correspond très bien à votre recherche : ...") and sent back to Spring Boot.

---

## 4. NLP Processor (`app/models/nlp_processor.py`)

A tailored utility module that bridges basic text processing gaps:
- It uses `langdetect` to figure out if text is French or English.
- Uses traditional NLP techniques (tokenization, stop-word removal) to pull out the most important words from a text. 

---

## 5. Text Extractor (`app/services/text_extractor.py`)

Handles raw binary streams uploaded by Instructors during the course creation step:
- **PyPDF2**: For parsing `.pdf` files.
- **python-docx**: For unwrapping `.docx` files.
- **python-pptx**: For reading text contained in `.pptx` slide shapes.
This acts purely as an in-memory stream processor before shipping the text back to Spring Boot. Data is not saved locally on the Python server.

---

## 6. Weak Topic Detector (`app/services/weak_topic_detector.py`)

A crucial component introduced to support the strict multi-chapter architecture. 
When a student fails a quiz (e.g., scores below a designated threshold), this service kicks in:
1. It analyzes the specific questions the student got wrong.
2. It projects those failed questions into vector space alongside the `Chapters` of the specific course.
3. It figures out what chapter the student misunderstood via Cosine Similarity (`distance(question_text, chapter_text)`).
4. Outputs an analysis flagging chapters with high error clustering as "critical", informing the student exactly what to review.
