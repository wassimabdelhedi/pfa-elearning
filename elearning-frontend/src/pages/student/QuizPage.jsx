import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { getPublishedQuizzes, submitQuizResult, getQuizById } from '../../api/quizApi';
import { FiCheckCircle, FiUser, FiClock, FiArrowLeft } from 'react-icons/fi';

export default function QuizPage() {
  const { id, courseId } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [quizzes, setQuizzes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeQuiz, setActiveQuiz] = useState(null);
  const [answers, setAnswers] = useState({});
  const [submitted, setSubmitted] = useState(false);
  const [score, setScore] = useState(null);

  useEffect(() => {
    loadQuizzes();
  }, [id]);

  const loadQuizzes = async () => {
    try {
      setLoading(true);
      let res;
      if (courseId) {
        const { getQuizzesByCourse } = await import('../../api/quizApi');
        res = await getQuizzesByCourse(courseId);
      } else {
        res = await getPublishedQuizzes();
      }
      setQuizzes(res.data);
      if (id) {
        try {
          const qRes = await getQuizById(id);
          startQuiz(qRes.data);
        } catch (e) {
          console.error("Quiz not found", e);
          closeQuizStateOnly();
        }
      } else {
        closeQuizStateOnly();
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const getLevelLabel = (level) => {
    const labels = { BEGINNER: 'Débutant', INTERMEDIATE: 'Intermédiaire', ADVANCED: 'Avancé' };
    return labels[level] || level;
  };

  const getLevelClass = (level) => {
    const classes = { BEGINNER: 'badge-beginner', INTERMEDIATE: 'badge-intermediate', ADVANCED: 'badge-advanced' };
    return classes[level] || 'badge-primary';
  };

  const startQuiz = (quiz) => {
    setActiveQuiz(quiz);
    setAnswers({});
    setSubmitted(false);
    setScore(null);
  };

  const selectAnswer = (questionIndex, optionIndex) => {
    if (submitted) return;
    setAnswers({ ...answers, [questionIndex]: optionIndex });
  };

  const submitQuiz = async () => {
    if (!activeQuiz || !activeQuiz.questions) return;
    let correct = 0;
    activeQuiz.questions.forEach((q, i) => {
      if (answers[i] === q.correctAnswer) {
        correct++;
      }
    });
    setScore(correct);
    setSubmitted(true);

    // Save result to backend
    try {
      await submitQuizResult(activeQuiz.id, {
        score: correct,
        totalQuestions: activeQuiz.questions.length
      });
    } catch (err) {
      console.error('Could not save quiz result:', err);
    }
  };

  const closeQuiz = () => {
    closeQuizStateOnly();
    if (courseId) {
      navigate(`/course/${courseId}/quizzes`);
    } else {
      navigate('/quiz');
    }
  };

  const closeQuizStateOnly = () => {
    setActiveQuiz(null);
    setAnswers({});
    setSubmitted(false);
    setScore(null);
  };

  if (loading) {
    return <div className="loading-container"><div className="spinner"></div></div>;
  }

  // Active quiz view
  if (activeQuiz) {
    return (
      <div className="page" style={{ maxWidth: 800, margin: '0 auto' }}>
        <div className="page-header">
          <h1>📋 {activeQuiz.title}</h1>
          <p>{activeQuiz.description}</p>
        </div>

        {submitted && (
          <div className="card" style={{ padding: 24, marginBottom: 24, textAlign: 'center' }}>
            <h2 style={{ fontSize: '1.5rem', marginBottom: 8 }}>
              {score >= activeQuiz.questions.length * 0.7 ? '🎉' : '📊'} Résultat
            </h2>
            <p style={{ fontSize: '1.2rem', color: score >= activeQuiz.questions.length * 0.7 ? '#4ade80' : '#fbbf24' }}>
              {score} / {activeQuiz.questions.length} réponses correctes
              ({Math.round((score / activeQuiz.questions.length) * 100)}%)
            </p>
          </div>
        )}

        {activeQuiz.questions && activeQuiz.questions.map((question, qIndex) => (
          <div key={qIndex} className="card" style={{ padding: 24, marginBottom: 16 }}>
            <h3 style={{ marginBottom: 16, fontSize: '1rem' }}>
              <span style={{ color: 'var(--primary-400)', marginRight: 8 }}>Q{qIndex + 1}.</span>
              {question.text}
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
              {question.options && question.options.map((option, oIndex) => {
                let optionStyle = {
                  padding: '12px 16px',
                  borderRadius: 8,
                  cursor: submitted ? 'default' : 'pointer',
                  transition: 'all 0.2s ease',
                  border: '1px solid rgba(255,255,255,0.1)',
                  background: answers[qIndex] === oIndex ? 'rgba(99, 102, 241, 0.2)' : 'rgba(255,255,255,0.03)',
                  color: 'var(--text-primary)',
                };

                if (submitted) {
                  if (oIndex === question.correctAnswer) {
                    optionStyle.background = 'rgba(34, 197, 94, 0.2)';
                    optionStyle.borderColor = 'rgba(34, 197, 94, 0.4)';
                  } else if (answers[qIndex] === oIndex && oIndex !== question.correctAnswer) {
                    optionStyle.background = 'rgba(239, 68, 68, 0.2)';
                    optionStyle.borderColor = 'rgba(239, 68, 68, 0.4)';
                  }
                }

                return (
                  <div
                    key={oIndex}
                    style={optionStyle}
                    onClick={() => selectAnswer(qIndex, oIndex)}
                  >
                    <span style={{ fontWeight: 600, marginRight: 10, color: 'var(--primary-300)' }}>
                      {String.fromCharCode(65 + oIndex)}.
                    </span>
                    {option}
                  </div>
                );
              })}
            </div>
          </div>
        ))}

        <div style={{ display: 'flex', gap: 12, marginTop: 20 }}>
          {!submitted ? (
            user?.role !== 'TEACHER' ? (
              <button
                className="btn btn-primary btn-lg"
                onClick={submitQuiz}
                disabled={Object.keys(answers).length < (activeQuiz.questions?.length || 0)}
                style={{ flex: 1 }}
              >
                <FiCheckCircle size={16} /> Soumettre les réponses
              </button>
            ) : (
              <div style={{ flex: 1, padding: 16, background: 'rgba(255,255,255,0.05)', borderRadius: 8, color: 'var(--text-secondary)', textAlign: 'center' }}>
                Mode Aperçu Enseignant
              </div>
            )
          ) : (
            <button className="btn btn-primary btn-lg" onClick={closeQuiz} style={{ flex: 1 }}>
              Retour aux quiz
            </button>
          )}
          {!submitted && (
            <button className="btn btn-secondary btn-lg" onClick={closeQuiz}>
              Annuler
            </button>
          )}
        </div>
      </div>
    );
  }

  // Quiz list view
  return (
    <div className="page">
      <div className="page-header">
        <h1>📋 Quiz</h1>
        <p>{quizzes.length} quiz disponibles</p>
      </div>

      {quizzes.length > 0 ? (
        <div className="course-grid">
          {quizzes.map(quiz => (
            <div key={quiz.id} className="card course-card animate-in">
              <div className="course-card-header">
                {quiz.categoryName && (
                  <span className="badge badge-primary">{quiz.categoryName}</span>
                )}
                {quiz.level && (
                  <span className={`badge ${getLevelClass(quiz.level)}`}>
                    {getLevelLabel(quiz.level)}
                  </span>
                )}
              </div>
              <div className="course-card-body">
                <h3>{quiz.title}</h3>
                <p>{quiz.description}</p>
                {quiz.questionsCount != null && (
                  <p style={{ fontSize: '0.8rem', color: 'var(--accent-400)', marginTop: 8 }}>
                    📝 {quiz.questionsCount} questions
                  </p>
                )}
              </div>
              <div className="course-card-footer">
                <span className="course-meta">
                  <FiUser size={13} /> {quiz.teacherName}
                </span>
                <button
                  className="btn btn-primary btn-sm"
                  onClick={() => startQuiz(quiz)}
                >
                  Commencer
                </button>
              </div>
            </div>
          ))}
        </div>
      ) : (
        <div style={{ textAlign: 'center', padding: 60, color: 'var(--text-secondary)' }}>
          Aucun quiz disponible pour le moment
        </div>
      )}
    </div>
  );
}
