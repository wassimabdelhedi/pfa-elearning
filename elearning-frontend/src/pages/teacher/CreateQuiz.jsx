import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { createQuiz } from '../../api/quizApi';
import { getMyTeacherCourses } from '../../api/courseApi';
import { FiPlusCircle, FiTrash2 } from 'react-icons/fi';

export default function CreateQuiz() {
  const [form, setForm] = useState({
    title: '', description: '', categoryId: '', courseId: '', level: 'BEGINNER', published: false
  });
  const [questions, setQuestions] = useState([
    { text: '', options: ['', '', '', ''], correctAnswer: 0 }
  ]);
  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    getMyTeacherCourses().then(res => setCourses(res.data)).catch(() => {});
  }, []);

  const handleChange = (e) => {
    const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value;
    setForm({ ...form, [e.target.name]: value });
  };

  const addQuestion = () => {
    setQuestions([...questions, { text: '', options: ['', '', '', ''], correctAnswer: 0 }]);
  };

  const removeQuestion = (index) => {
    if (questions.length <= 1) return;
    setQuestions(questions.filter((_, i) => i !== index));
  };

  const updateQuestion = (index, field, value) => {
    const updated = [...questions];
    updated[index][field] = value;
    setQuestions(updated);
  };

  const updateOption = (qIndex, oIndex, value) => {
    const updated = [...questions];
    updated[qIndex].options[oIndex] = value;
    setQuestions(updated);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    // Validate questions
    for (let i = 0; i < questions.length; i++) {
      if (!questions[i].text.trim()) {
        setError(`La question ${i + 1} est vide`);
        setLoading(false);
        return;
      }
      for (let j = 0; j < questions[i].options.length; j++) {
        if (!questions[i].options[j].trim()) {
          setError(`L'option ${j + 1} de la question ${i + 1} est vide`);
          setLoading(false);
          return;
        }
      }
    }

    try {
      const payload = {
        ...form,
        courseId: form.courseId || null,
        questions
      };
      await createQuiz(payload);
      navigate('/teacher/dashboard');
    } catch (err) {
      setError(err.response?.data?.message || 'Erreur lors de la création');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page" style={{ maxWidth: 800, margin: '0 auto' }}>
      <div className="page-header">
        <h1>📋 Nouveau quiz</h1>
        <p>Créez un quiz interactif pour évaluer vos étudiants</p>
      </div>

      <div className="card" style={{ padding: 32 }}>
        {error && <div className="error-message" style={{ marginBottom: 20 }}>{error}</div>}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
          <div className="form-group">
            <label htmlFor="title">Titre du quiz *</label>
            <input id="title" name="title" className="form-input"
              placeholder="Ex: Quiz Python - Les bases" value={form.title}
              onChange={handleChange} required />
          </div>

          <div className="form-group">
            <label htmlFor="description">Description</label>
            <textarea id="description" name="description" className="form-input"
              placeholder="Décrivez l'objectif de ce quiz..."
              value={form.description} onChange={handleChange} rows={2} />
          </div>

          <div className="form-group">
            <label htmlFor="level">Niveau</label>
            <select id="level" name="level" className="form-input"
              value={form.level} onChange={handleChange}>
              <option value="BEGINNER">🟢 Débutant</option>
              <option value="INTERMEDIATE">🟡 Intermédiaire</option>
              <option value="ADVANCED">🔴 Avancé</option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="courseId">Cours associé (optionnel)</label>
            <select id="courseId" name="courseId" className="form-input"
              value={form.courseId} onChange={handleChange}>
              <option value="">-- Aucun --</option>
              {courses.map(course => (
                <option key={course.id} value={course.id}>{course.title}</option>
              ))}
            </select>
          </div>

          {/* Questions */}
          <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: 20 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
              <h3 style={{ fontSize: '1.1rem' }}>Questions ({questions.length})</h3>
              <button type="button" className="btn btn-secondary btn-sm" onClick={addQuestion}>
                <FiPlusCircle size={14} /> Ajouter une question
              </button>
            </div>

            {questions.map((q, qIndex) => (
              <div key={qIndex} className="card" style={{ padding: 20, marginBottom: 16, background: 'rgba(255,255,255,0.02)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
                  <span style={{ fontWeight: 600, color: 'var(--primary-300)' }}>Question {qIndex + 1}</span>
                  {questions.length > 1 && (
                    <button type="button" className="btn btn-danger btn-sm" onClick={() => removeQuestion(qIndex)}>
                      <FiTrash2 size={12} />
                    </button>
                  )}
                </div>

                <div className="form-group" style={{ marginBottom: 12 }}>
                  <input className="form-input" placeholder="Tapez la question..."
                    value={q.text} onChange={(e) => updateQuestion(qIndex, 'text', e.target.value)} />
                </div>

                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                  {q.options.map((opt, oIndex) => (
                    <div key={oIndex} style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                      <input
                        type="radio"
                        name={`correct-${qIndex}`}
                        checked={q.correctAnswer === oIndex}
                        onChange={() => updateQuestion(qIndex, 'correctAnswer', oIndex)}
                        style={{ accentColor: 'var(--primary-500)', width: 18, height: 18 }}
                        title="Bonne réponse"
                      />
                      <input
                        className="form-input"
                        placeholder={`Option ${String.fromCharCode(65 + oIndex)}`}
                        value={opt}
                        onChange={(e) => updateOption(qIndex, oIndex, e.target.value)}
                        style={{ flex: 1 }}
                      />
                    </div>
                  ))}
                  <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: 4 }}>
                    Sélectionnez le bouton radio à côté de la bonne réponse
                  </p>
                </div>
              </div>
            ))}
          </div>

          <label style={{ display: 'flex', alignItems: 'center', gap: 10, cursor: 'pointer' }}>
            <input type="checkbox" name="published" checked={form.published}
              onChange={handleChange} style={{ width: 18, height: 18, accentColor: 'var(--primary-500)' }} />
            <span style={{ color: 'var(--text-secondary)' }}>
              Publier immédiatement
            </span>
          </label>

          <div style={{ display: 'flex', gap: 12 }}>
            <button type="submit" className="btn btn-primary btn-lg" disabled={loading} style={{ flex: 1 }}>
              {loading ? 'Création en cours...' : '🚀 Créer le quiz'}
            </button>
            <button type="button" className="btn btn-secondary btn-lg"
              onClick={() => navigate('/teacher/dashboard')}>
              Annuler
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
