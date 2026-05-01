# AI Recommendation Engine Architecture (LearnAgent)

This document provides a detailed, comprehensive explanation of the Python AI Recommendation Engine (`ai-recommendation-engine`), which handles semantic search, document processing, and recommendation logic for the LearnAgent e-learning platform.

## 0.1 Feature Overview

The AI engine provides the following key capabilities:

- **Course Recommendation**: Generates personalized course suggestions based on semantic similarity between learner queries and course content.
- **Semantic Search**: Enables language‑agnostic search across French, English, and Arabic using dense embeddings.
- **Document Text Extraction**: Parses PDFs, DOCX, PPTX, and TXT files to extract raw text for indexing.
- **Keyword Extraction**: Derives salient keywords from course material using spaCy and TF‑IDF.
- **Weak Topic Detection**: Identifies student knowledge gaps by comparing quiz answers to course chapters.
- **Explainable AI**: Produces human‑readable reasons for each recommendation.
- **Model Management & Caching**: Handles model loading, hot‑reload, and embedding caching with Redis.
- **Monitoring & Logging**: Exposes health checks, Prometheus metrics, and structured JSON logs.
- **Security & Rate Limiting**: Protects endpoints with API keys and request throttling.
- **Testing & CI/CD**: Includes unit, integration tests and automated pipelines.

These features are exposed via a set of FastAPI endpoints detailed below.

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

**Semantic Search** – The engine leverages Sentence‑Transformers to generate dense embeddings for both user queries and course content, enabling language‑agnostic similarity matching across French, English, and Arabic resources.

- **Hot‑Reload** – In development mode, a `/api/reload-model` endpoint can be triggered to reload the transformer model without restarting the service, useful when experimenting with alternative pretrained models.

### Data Flow Overview
The AI engine receives requests from the Spring Boot backend via the FastAPI endpoints. The typical flow is:
1. **Request** – The backend sends a JSON payload (e.g., search query) to `/api/recommend`.
2. **Processing** – FastAPI forwards the query to the recommender service which computes embeddings using the Sentence‑Transformer model.
3. **Similarity Search** – The service retrieves candidate course embeddings from the Redis cache and calculates cosine similarity.
4. **Ranking & Explanation** – Results are weighted, ranked, and an optional explanation is generated (Explainable AI).
5. **Response** – A JSON response with recommended courses and reasons is returned to the backend, which then forwards it to the front‑end UI.

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

## 7. Keyword Extraction Service

The engine also provides a lightweight **keyword extraction** endpoint (`/api/extract-keywords`). This service leverages **spaCy**'s statistical model combined with TF‑IDF weighting to surface the most representative terms from any given course material. These keywords are later stored as searchable tags in the Spring Boot catalog, enabling fast filtering and faceted search on the e‑learning platform.

## 8. Model Management & Caching

- **Model Loading** – The Sentence‑Transformer model (`paraphrase‑multilingual‑MiniLM‑L12‑v2`) is loaded once at server startup and kept in memory to avoid repeated GPU/CPU warm‑up costs.
- **Embedding Cache** – Course embeddings are persisted in a Redis cache (or SQLite for local dev). When a new course is indexed via `/api/index-course`, its vector is computed and stored, allowing subsequent recommendation queries to perform pure similarity look‑ups without recomputing embeddings.
- **Hot‑Reload** – In development mode, a `/api/reload-model` endpoint can be triggered to reload the transformer model without restarting the service, useful when experimenting with alternative pretrained models.

## 9. Model Training
To ensure high accuracy, the model undergoes periodic fine-tuning on domain-specific datasets. This involves leveraging triplet-loss functions on annotated student interaction data to align course content representations closer to learner search intent.

## 10. Explainable AI
To build user trust, the system generates rationale for its recommendations. By performing attribution analysis on the feature vectors, the engine identifies which specific keywords or chapters contributed most to a course's similarity score, providing human-readable justifications like "Recommended because you have studied similar concepts in [Chapter X]".

## 11. Deployment, Scaling & Monitoring

The AI microservice is packaged as a lightweight Docker container, exposing port `8000`. It can be horizontally scaled behind an NGINX reverse‑proxy or Kubernetes Service, each instance sharing the same Redis cache for embeddings. Health‑checks (`/health`) report the model load status, and Prometheus metrics (`/metrics`) expose request latency, cache hit‑ratio, and inference GPU utilization. Logs are structured JSON, facilitating centralized log aggregation (e.g., ELK stack) for debugging and audit trails.

### Logging & Monitoring

The service uses structured JSON logs with fields for timestamp, level, request ID, and operation details. Logs are shipped to a centralized Logstash instance and visualized in Kibana. In addition to Prometheus metrics, the microservice exposes a `/logs` endpoint (protected by API key) to retrieve recent logs for troubleshooting.

### Performance Optimizations

- **Batch Embedding**: When indexing a batch of new courses, embeddings are computed in parallel using a thread pool.
- **Cache Warm‑up**: On startup, the service pre‑loads the most frequently accessed course embeddings into Redis to reduce cold‑start latency.
- **Async I/O**: FastAPI's async endpoints ensure non‑blocking handling of file uploads and inference calls.

## 12. Model Evaluation & Performance Metrics

The recommendation model is periodically evaluated on a hold‑out validation set representing a diverse mix of languages (French, English, Arabic). Key metrics:

- **Mean Reciprocal Rank (MRR)** – measures the rank position of the first relevant course.
- **Recall@10** – proportion of queries where a relevant course appears in the top‑10 results.
- **Latency** – average inference time per request (≈ 30 ms on CPU, 8 ms on GPU).

Results are logged to Prometheus (`/metrics`) and visualized in Grafana dashboards, enabling continuous monitoring and quick detection of regressions after model updates.

## 13. Security, Rate Limiting & Access Control

All endpoints are protected by an API key issued to the Spring Boot backend. The FastAPI app validates the `X‑API‑KEY` header against a secret stored in environment variables. Additional safeguards:

- **Rate limiting** – 100 requests per minute per API key, enforced via the `slowapi` middleware.
- **Input validation** – Pydantic schemas ensure request payloads conform to expected types and sizes.
- **CORS** – Restricted to the internal network (`http://localhost:8080`).

These measures prevent abuse and ensure only authorized services can invoke the AI engine.

## 14. Testing, CI/CD & Documentation

- **Unit tests** – located in `tests/`, covering each service layer with `pytest` and `coverage` (> 90%).
- **Integration tests** – spin up a Docker Compose environment with the AI service, Redis, and a mock Spring Boot client.
- **CI pipeline** – GitHub Actions run linting (`flake8`), type checking (`mypy`), and test suites on each push.
- **Auto‑generated OpenAPI docs** – accessible at `/docs` and `/redoc`, providing developers with live API specifications.

These practices guarantee reliable releases and easy onboarding for new contributors.

## 16. Future Work & Enhancements & Enhancements

- **Multilingual Expansion** – Incorporate additional language models to support emerging markets.
- **Personalized Ranking** – Integrate user interaction data (click‑through, completion rates) to refine recommendation scores via learning‑to‑rank algorithms.

## 17. Conclusion

The AI Recommendation Engine serves as the intelligent core of the LearnAgent platform, offering scalable, performant, and secure AI‑driven functionalities that enhance personalized learning experiences. Continuous evaluation and iterative improvements ensure the system remains state‑of‑the‑art and aligns with educational objectives.
