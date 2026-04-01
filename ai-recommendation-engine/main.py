"""
AI Recommendation Engine - FastAPI
Moteur de recommandation intelligent pour e-learning
Utilise Sentence-BERT multilingue (FR/EN) pour la similarité sémantique
"""

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.routes.recommendation_routes import router as recommendation_router

app = FastAPI(
    title="LearnAgent AI Recommendation Engine",
    description="Moteur de recommandation IA basé sur Sentence-BERT pour la plateforme LearnAgent",
    version="1.0.0"
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8080", "http://localhost:5173","http://localhost:5174"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Routes
app.include_router(recommendation_router, prefix="/api", tags=["Recommendations"])


@app.get("/")
def root():
    return {
        "service": "LearnAgent AI Recommendation Engine",
        "status": "running",
        "model": "paraphrase-multilingual-MiniLM-L12-v2",
        "languages": ["fr", "en"]
    }


@app.get("/health")
def health_check():
    return {"status": "healthy"}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=8000, reload=True)
