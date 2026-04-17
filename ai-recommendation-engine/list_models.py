import os
from google import genai
from dotenv import load_dotenv

load_dotenv()
api_key = os.getenv("GEMINI_API_KEY")

if not api_key:
    print("ERREUR : GEMINI_API_KEY non trouvée dans le fichier .env")
    exit()

print(f"Test de la clé API : {api_key[:10]}...")
client = genai.Client(api_key=api_key)

try:
    print("\nListe brute des modèles disponibles :")
    for model in client.models.list():
        print(f" - {model.name}")
except Exception as e:
    print(f"\nErreur lors du listing des modèles : {e}")
