import requests
import json
import time

BASE_URL = "http://localhost:8081/api"

def get_token(email, password, role):
    print(f"Authenticating as {role} ({email})...")
    login_data = {"email": email, "password": password}
    r = requests.post(f"{BASE_URL}/auth/login", json=login_data)
    
    if r.status_code == 401:
        print(f"Login failed, registering new {role}...")
        register_data = {
            "firstName": "Test",
            "lastName": role.capitalize(),
            "email": email,
            "password": password,
            "role": role
        }
        r = requests.post(f"{BASE_URL}/auth/register", json=register_data)
        r.raise_for_status()
        print("Registration successful.")
        return r.json()["token"]
    
    r.raise_for_status()
    return r.json()["token"]

def test_flow():
    print("--- Starting End-to-End Test for Weak Topic Detection ---")
    
    # 1. Teacher creates the quiz
    teacher_token = get_token("teacher@test.com", "password123", "TEACHER")
    teacher_headers = {"Authorization": f"Bearer {teacher_token}"}

    print("\n1.5 Creating a test course...")
    course_payload = {
        "title": (None, "Java Programming Basics"),
        "description": (None, "Learn the foundations of Java"),
        "categoryId": (None, "3"),
        "level": (None, "BEGINNER"),
        "published": (None, "true")
    }
    # Don't use teacher_headers here as we need requests to set the multipart boundary
    r = requests.post(f"{BASE_URL}/courses", files=course_payload, headers={"Authorization": f"Bearer {teacher_token}"})
    r.raise_for_status()
    course_id = r.json()["id"]
    print(f"Course created with ID: {course_id}")

    print("\n2. Creating a test quiz with topics...")
    quiz_data = {
        "title": "Java Mastery Quiz",
        "description": "Quiz testing inheritance and polymorphism",
        "level": "INTERMEDIATE",
        "categoryId": 3,
        "courseId": course_id,
        "published": True,
        "questions": [
            {
                "text": "Which keyword is used for inheritance in Java?",
                "topic": "Héritage",
                "options": ["implements", "extends", "inherits"],
                "correctAnswer": 1
            },
            {
                "text": "Can a class implement multiple interfaces?",
                "topic": "Interfaces",
                "options": ["Yes", "No", "Only one"],
                "correctAnswer": 0
            },
            {
                "text": "What is method overriding in Java?",
                "topic": "Polymorphisme",
                "options": ["Static binding", "Dynamic binding", "No binding"],
                "correctAnswer": 1
            }
        ]
    }
    
    r = requests.post(f"{BASE_URL}/quizzes", json=quiz_data, headers=teacher_headers)
    r.raise_for_status()
    quiz = r.json()
    quiz_id = quiz["id"]
    print(f"Quiz created with ID: {quiz_id}")

    # 2. Student submits the quiz
    student_token = get_token("student@test.com", "password123", "STUDENT")
    student_headers = {"Authorization": f"Bearer {student_token}"}

    print("\n3. Submitting quiz as student with errors (score 1/3)...")
    submission_data = {
        "score": 1,
        "totalQuestions": 3,
        "answers": [
            {
                "questionId": quiz["questions"][0]["id"],
                "studentAnswerIndex": 0 # Wrong (Inheritance) - used 'implements'
            },
            {
                "questionId": quiz["questions"][1]["id"],
                "studentAnswerIndex": 0 # Correct (Interfaces) - 'Yes'
            },
            {
                "questionId": quiz["questions"][2]["id"],
                "studentAnswerIndex": 0 # Wrong (Polymorphism) - 'Static binding'
            }
        ]
    }
    
    r = requests.post(f"{BASE_URL}/quizzes/{quiz_id}/submit", json=submission_data, headers=student_headers)
    r.raise_for_status()
    result = r.json()
    
    print("\n" + "="*40)
    print("TEST RESULT FROM BACKEND")
    print("="*40)
    print(f"Quiz: {result['quizTitle']}")
    print(f"Score: {result['score']}/{result['totalQuestions']} ({result['percentage']}%)")
    print(f"Failed Status: {result['failed']}")
    
    print("\nWeak Topics Detected by AI:")
    if result.get("weakTopics"):
        weak_topics = json.loads(result["weakTopics"])
        for wt in weak_topics:
            print(f" [!] Topic: {wt['topic']: <15} | Severity: {wt['severity'].upper()}")
    else:
        print("[!] No weak topics detected (check if AI engine is running on port 8000)")
    print("="*40)

if __name__ == "__main__":
    try:
        test_flow()
    except Exception as e:
        print(f"\nERROR: {e}")
        if hasattr(e, 'response') and e.response is not None:
            print(f"Response: {e.response.text}")
